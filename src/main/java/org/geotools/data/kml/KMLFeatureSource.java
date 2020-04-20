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

import java.io.IOException;
import javax.xml.namespace.QName;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/** @author Niels Charlier, Scitus Development */
public class KMLFeatureSource extends ContentFeatureSource {

    public KMLFeatureSource(ContentEntry entry, Query query) {
        super(entry, query);
    }

    @Override
    protected QueryCapabilities buildQueryCapabilities() {
        return new QueryCapabilities() {
            public boolean isUseProvidedFIDSupported() {
                return true;
            }
        };
    }

    @Override
    protected ReferencedEnvelope getBoundsInternal(Query query) throws IOException {
        ReferencedEnvelope bounds =
                new ReferencedEnvelope(getSchema().getCoordinateReferenceSystem());

        FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = getReaderInternal(query);
        try {
            while (featureReader.hasNext()) {
                SimpleFeature feature = featureReader.next();
                bounds.include(feature.getBounds());
            }
        } finally {
            featureReader.close();
        }
        return bounds;
    }

    @Override
    protected int getCountInternal(Query query) throws IOException {
        int count = 0;
        FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = getReaderInternal(query);
        try {
            while (featureReader.hasNext()) {
                featureReader.next();
                count++;
            }
        } finally {
            featureReader.close();
        }
        return count;
    }

    @Override
    protected SimpleFeatureType buildFeatureType() throws IOException {
        String typeName = getEntry().getTypeName();
        String namespace = getEntry().getName().getNamespaceURI();

        SimpleFeatureType type;
        FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = getReaderInternal(query);
        try {
            type = featureReader.getFeatureType();
        } finally {
            featureReader.close();
        }

        // rename
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        if (type != null) {
            b.init(type);
        }
        b.setName(typeName);
        b.setNamespaceURI(namespace);
        return b.buildFeatureType();
    }

    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(Query query)
            throws IOException {
        KMLDataStore dataStore = (KMLDataStore) getEntry().getDataStore();
        return new KMLFeatureReader(
                dataStore.getNamespaceURI(),
                dataStore.file,
                new QName(getEntry().getName().getNamespaceURI(), getEntry().getTypeName()));
    }
}