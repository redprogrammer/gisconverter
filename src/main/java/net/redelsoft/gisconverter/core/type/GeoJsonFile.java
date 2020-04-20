/*
 * The MIT License
 *
 * Copyright 2020 Emre Demir.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.redelsoft.gisconverter.core.type;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.geojson.GeoJSONDataStore;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * GeoJSON File Process Implementation
 *
 * @author Emre Demir
 */
public class GeoJsonFile extends FileProcess {

    @Override
    public FeatureSource readFile(File file) {
        GeoJSONDataStore ds = null;
        try {
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(GeoJSONDataStoreFactory.FILE_PARAM.key, file);

            GeoJSONDataStoreFactory factory = new GeoJSONDataStoreFactory();
            ds = (GeoJSONDataStore) factory.createDataStore(params);

            return ds.getFeatureSource(ds.getTypeNames()[0]);
        } catch (Exception ex) {
            throw new RuntimeException("Error while reading GeoJSON File!!!", ex);
        } finally {
            if (ds != null) {
                ds.dispose();
            }
        }
    }

    @Override
    public void writeFile(FeatureSource fs, File file) {
        try {
            SimpleFeatureCollection sfc = ff.getSingleGeomColumnSFC(fs);
            SimpleFeatureType schema = sfc.getSchema();

            GeoJSONDataStoreFactory dsFactory = new GeoJSONDataStoreFactory();
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(GeoJSONDataStoreFactory.FILE_PARAM.key, file);

            GeoJSONDataStore ds = (GeoJSONDataStore) dsFactory.createNewDataStore(params);
            ds.createSchema(schema);

            Transaction transaction = new DefaultTransaction("create");

            String typeName = ds.getTypeNames()[0];
            SimpleFeatureSource featureSource = ds.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(sfc);

                    transaction.commit();

                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                } finally {
                    transaction.close();
                }
            } else {
                throw new RuntimeException("Can't write to new GeoJSONDatastore");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while writing GeoJSON File!!!", ex);
        }
    }

}
