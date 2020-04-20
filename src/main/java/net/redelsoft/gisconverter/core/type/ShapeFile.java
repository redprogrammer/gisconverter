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
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;

/**
 * Shape File Process Implementation
 *
 * @author Emre Demir
 */
public class ShapeFile extends FileProcess {

    @Override
    public FeatureSource readFile(File file) {
        ShapefileDataStore ds = null;
        try {
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());

            ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
            ds = (ShapefileDataStore) factory.createDataStore(params);
            return ds.getFeatureSource();
        } catch (Exception e) {
            throw new RuntimeException("Error while reading Shape File!!!", e);
        } finally {
            if (ds != null) {
                ds.dispose();
            }
        }
    }

    @Override
    public void writeFile(FeatureSource fs, File file) {

        try {
            SimpleFeatureCollection collection = ff.getFeatureCollection(fs);

            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("url", file.toURI().toURL());
            params.put("create spatial index", Boolean.TRUE);

            ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
            dataStore.createSchema(collection.getSchema());
            dataStore.forceSchemaCRS(collection.getSchema().getCoordinateReferenceSystem());

            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                Transaction transaction = new DefaultTransaction("create");
                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(collection);
                    transaction.commit();

                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                } finally {
                    transaction.close();
                }
            } else {
                throw new RuntimeException("Can't write to new ShapeFileDatastore");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while writing GeoJSON File!!!", ex);
        }
    }

}
