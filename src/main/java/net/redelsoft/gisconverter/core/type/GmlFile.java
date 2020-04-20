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
import java.io.InputStream;
import java.io.OutputStream;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.wfs.GML;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * GML File Process Implementation
 *
 * @author Emre Demir
 */
public class GmlFile extends FileProcess {

    @Override
    public FeatureSource readFile(File file) {
        try {
            GML gml = null;
            SimpleFeatureCollection fc = null;
            try {
                InputStream in = file.toURI().toURL().openStream();
                gml = new GML(GML.Version.WFS1_0);
                fc = gml.decodeFeatureCollection(in);
                in.close();
            } catch (Exception e) {
                try {
                    InputStream in = file.toURI().toURL().openStream();
                    gml = new GML(GML.Version.WFS1_1);
                    fc = gml.decodeFeatureCollection(in);
                    in.close();
                } catch (Exception ex) {
                    throw ex;
                }
            }

            if (fc != null) {
                return DataUtilities.source(fc);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while reading GML File!!!", e);
        }

        return null;
    }

    @Override
    public void writeFile(FeatureSource fs, File file) {
        try {
            SimpleFeatureType schema = ff.fixNullCRSWithWGS84((SimpleFeatureType) fs.getSchema());
            OutputStream out = new FileOutputStream(file);
            GML gml = new GML(GML.Version.WFS1_0);
            gml.setNamespace("geotools", "http://geotools.org");
            gml.setCoordinateReferenceSystem(schema.getCoordinateReferenceSystem());
            
            gml.encode(out, (SimpleFeatureCollection) fs.getFeatures());
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException("Error while writing GML File!!!", ex);
        }
    }

}
