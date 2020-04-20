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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.redelsoft.gisconverter.core.FeatureFactory;
import org.geotools.data.FeatureSource;
import org.geotools.data.kml.KMLDataStore;
import org.geotools.data.kml.KMLDataStoreFactory;
import org.geotools.data.kml.KMLFeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xsd.Encoder;

/**
 * KML File Process Implementation
 *
 * @author Emre Demir
 */
public class KmlFile extends FileProcess {

    @Override
    public FeatureSource readFile(File file) {
        KMLDataStore ds = null;
        try {
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(KMLDataStoreFactory.FILE.key, file);

            KMLDataStoreFactory factory = new KMLDataStoreFactory();
            ds = (KMLDataStore) factory.createDataStore(params);

            return (KMLFeatureSource) ds.getFeatureSource(ds.getTypeNames()[0]);
        } catch (Exception ex) {
            throw new RuntimeException("Error while reading KML File!!!", ex);
        } finally {
            if (ds != null) {
                ds.dispose();
            }
        }
    }

    @Override
    public void writeFile(FeatureSource fs, File file) {
        try {
            FeatureFactory ff = new FeatureFactory();
            SimpleFeatureCollection kmlFeatureCollection = ff.getKmlFeatureCollection(fs);

            OutputStream out = new FileOutputStream(file);
            Encoder encoder = new Encoder(new KMLConfiguration());
            encoder.setIndenting(true);

            encoder.encode(kmlFeatureCollection, KML.kml, out);
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error while writing KML File!!!", ex);
        }
    }

}
