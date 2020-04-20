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
package org.geotools.data.kml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.geotools.data.FeatureReader;
import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Read a KML file directly.
 *
 * @author Niels Charlier, Scitus Development
 */
public class KMLFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    private static final Logger LOGGER = Logging.getLogger(KMLFeatureReader.class);

    private SimpleFeatureType type = null;
    private SimpleFeature f = null;
    private org.geotools.xsd.StreamingParser parser;
    private FileInputStream fis = null;

    public KMLFeatureReader(String namespace, File file, QName name) throws IOException {
        try {
            fis = new FileInputStream(file);
            parser
                    = new org.geotools.xsd.StreamingParser(
                            new KMLConfiguration(), fis, KML.Placemark);
        } catch (Exception e) {
            throw new IOException("Error processing KML file", e);
        }
        forward();
        if (f != null) {
            type = f.getType();
        }
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return type;
    }

    /**
     * Grab the next feature from the property file.
     *
     * @return feature
     * @throws NoSuchElementException Check hasNext() to avoid reading off the
     * end of the file
     */
    @Override
    public SimpleFeature next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        SimpleFeature next = f;
        forward();
        return next;
    }

    public void forward() {
        try {
            f = (SimpleFeature) parser.parse();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     *      */
    @Override
    public boolean hasNext() {
        return f != null;
    }

    /**
     * Be sure to call close when you are finished with this reader; as it must
     * close the file it has open.
     *
     */
    @Override
    public void close() {
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(KMLFeatureReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
