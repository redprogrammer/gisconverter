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
import net.redelsoft.gisconverter.core.FeatureFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.csv.CSVDataStore;
import org.geotools.data.csv.CSVDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * CSV File Process Implementation
 * 
 * @author Emre Demir
 */
public class CsvFile extends FileProcess {

    @Override
    public FeatureSource readFile(File file) {
        CSVDataStore ds = null;
        try {
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(CSVDataStoreFactory.FILE_PARAM.key, file);
            params.put(CSVDataStoreFactory.STRATEGYP.key, CSVDataStoreFactory.GUESS_STRATEGY);

            CSVDataStoreFactory factory = new CSVDataStoreFactory();
            ds = (CSVDataStore) factory.createDataStore(params);

            String typeName = ds.getTypeNames()[0];

            SimpleFeatureCollection sfc = ff.getCsvFeatureCollection(ds.getFeatureSource(typeName));

            return DataUtilities.source(sfc);
        } catch (Exception ex) {
            throw new RuntimeException("Error while reading CSV File!!!", ex);
        } finally {
            if (ds != null) {
                ds.dispose();
            }
        }
    }

    @Override
    public void writeFile(FeatureSource fs, File file) {
        DataStore dataStore = null;
        try {
            SimpleFeatureCollection sfc = ff.getSingleGeomColumnSFC(fs);
            SimpleFeatureType schema = sfc.getSchema();
            FeatureCollection fc = fs.getFeatures();

            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(CSVDataStoreFactory.FILE_PARAM.key, file);
            params.put(CSVDataStoreFactory.STRATEGYP.key, CSVDataStoreFactory.WKT_STRATEGY);
            params.put(CSVDataStoreFactory.WKTP.key, FeatureFactory.GEOM_COLUMN_NAME);

            CSVDataStoreFactory factory = new CSVDataStoreFactory();
            dataStore = factory.createNewDataStore(params);
            dataStore.createSchema(schema);

            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = (SimpleFeatureSource) dataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore outStore = (SimpleFeatureStore) featureSource;
                Transaction transaction = new DefaultTransaction("create");
                outStore.setTransaction(transaction);

                try {
                    outStore.addFeatures(fc);
                    transaction.commit();

                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                } finally {
                    transaction.close();
                }
            } else {
                throw new RuntimeException("Can't write to new CSVDatastore");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while writing CSV File!!!",ex);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
    }

}
