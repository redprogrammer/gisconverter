/*
 * The MIT License
 *
 * Copyright 2020 Emre Demir.
 *
 * Permission    @Override
    public String getGeometryType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Coordinate getCoordinate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Coordinate[] getCoordinates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Geometry getBoundary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getBoundaryDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Geometry reverse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equalsExact(Geometry gmtr, double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void apply(CoordinateFilter cf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void apply(CoordinateSequenceFilter csf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void apply(GeometryFilter gf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void apply(GeometryComponentFilter gcf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Geometry copyInternal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void normalize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Envelope computeEnvelopeInternal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int compareToSameClass(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int compareToSameClass(Object o, CoordinateSequenceComparator csc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getSortIndex() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 is hereby granted, free of charge, to any person obtaining a copy
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
package net.redelsoft.gisconverter.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.geom.util.PointExtracter;
import org.locationtech.jts.geom.util.PolygonExtracter;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * FeatureFactory class creates new featuretypes and collections for the
 * specific file problems
 *
 * @author Emre Demir
 */
public class FeatureFactory {

    private static FeatureFactory instance = null;

    public static final String GEOM_COLUMN_NAME = "the_geom";
    public static final CoordinateReferenceSystem DEFAULT_CRS = DefaultGeographicCRS.WGS84;

    public FeatureFactory() {
    }

    public static FeatureFactory getInstance() {
        if (instance == null) {
            instance = new FeatureFactory();
        }

        return instance;
    }

    public SimpleFeatureCollection getCsvFeatureCollection(FeatureSource fs) {

        try {
            SimpleFeatureType csvfileFeatureType = getCsvfileFeatureType((SimpleFeatureSource) fs);
            Collection<PropertyDescriptor> descriptors = fs.getSchema().getDescriptors();
            List<SimpleFeature> featureList = new ArrayList<>();
            WKTReader2 wktReader = new WKTReader2();

            fs.getFeatures().accepts((Feature ftr) -> {
                try {
                    SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(csvfileFeatureType);
                    for (PropertyDescriptor descriptor : descriptors) {
                        String propName = descriptor.getName().getLocalPart();
                        if (propName.equals(csvfileFeatureType.getGeometryDescriptor().getLocalName())) {
                            String wktStr = (String) ftr.getProperty(propName).getValue();
                            sfb.set(propName, wktReader.read(wktStr));
                        } else {
                            sfb.set(propName, ftr.getProperty(propName).getValue());
                        }

                    }
                    featureList.add(sfb.buildFeature(null));
                } catch (ParseException ex) {
                    Logger.getLogger(FeatureFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }, null);

            return DataUtilities.collection(featureList);
        } catch (IOException ex) {
            Logger.getLogger(FeatureFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SimpleFeatureType getCsvfileFeatureType(FeatureSource fs) throws IOException {
        Query q = new Query();
        q.setMaxFeatures(1);
        SimpleFeature feature = null;
        if (fs.getFeatures(q).features().hasNext()) {
            feature = (SimpleFeature) fs.getFeatures(q).features().next();
        }

        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName(fs.getName().getLocalPart());
        sftb.setCRS(DEFAULT_CRS);

        Collection<PropertyDescriptor> descriptors = fs.getSchema().getDescriptors();
        WKTReader2 wktReader = new WKTReader2();
        for (PropertyDescriptor descriptor : descriptors) {
            String attrName = descriptor.getName().getLocalPart();
            if (descriptor.getType().getBinding().equals(String.class)) {
                try {
                    Geometry read = wktReader.read((String) feature.getProperty(attrName).getValue());
                    sftb.add(attrName, read.getClass());
                } catch (ParseException ex) {
                    sftb.add(attrName, descriptor.getType().getBinding());
                }
            } else {
                sftb.add(attrName, descriptor.getType().getBinding());
            }
        }

        return sftb.buildFeatureType();
    }

    public SimpleFeatureType getShapefileFeatureType(FeatureSource fs) throws IOException {
        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName(fs.getSchema().getName().getLocalPart());
        if (fs.getSchema().getCoordinateReferenceSystem() == null) {
            sftb.setCRS(DEFAULT_CRS);
        } else {
            sftb.setCRS(fs.getSchema().getCoordinateReferenceSystem());
        }

        Collection<PropertyDescriptor> descriptors = fs.getSchema().getDescriptors();
        String geometryDesc = fs.getSchema().getGeometryDescriptor().getName().getLocalPart();

        Class<?> geomBinding = fs.getSchema().getGeometryDescriptor().getType().getBinding();
        if (fs.getSchema().getGeometryDescriptor().getType().getBinding().equals(Geometry.class)) {
            if (fs.getFeatures().size() > 0) {
                Query q = new Query();
                q.setMaxFeatures(1);
                SimpleFeature sf = (SimpleFeature) fs.getFeatures(q).toArray()[0];
                //Class<?> geomTypeBinding = sf.getDefaultGeometryProperty().getType().getBinding();
                Geometry geom = (Geometry) sf.getDefaultGeometryProperty().getValue();
                if (geom instanceof Point || geom instanceof MultiPoint) {
                    geomBinding = MultiPoint.class;
                } else if (geom instanceof LineString || geom instanceof MultiLineString) {
                    geomBinding = MultiLineString.class;
                } else if (geom instanceof Polygon || geom instanceof MultiPolygon) {
                    geomBinding = MultiPolygon.class;
                }
            } else {

            }
        }

        sftb.add(GEOM_COLUMN_NAME, geomBinding);
        descriptors.stream()
                .filter((descriptor) -> (!geometryDesc.equals(descriptor.getName().getLocalPart())))
                .forEachOrdered((descriptor) -> {
                    sftb.nillable(descriptor.isNillable()).add(descriptor.getName().getLocalPart(), descriptor.getType().getBinding());
                });

        return sftb.buildFeatureType();
    }

    public SimpleFeatureCollection getFeatureCollection(FeatureSource fs) {
        try {
            Collection<PropertyDescriptor> descriptors = fs.getSchema().getDescriptors();
            String geometryDesc = fs.getSchema().getGeometryDescriptor().getName().getLocalPart();

            SimpleFeatureType ft = getShapefileFeatureType(fs);
            List<SimpleFeature> featureList = new ArrayList<>();

            fs.getFeatures().accepts((Feature ftr) -> {
                SimpleFeature sf = (SimpleFeature) ftr;
                SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(ft);
                descriptors.stream()
                        .filter((descriptor) -> (!geometryDesc.equals(descriptor.getName().getLocalPart())))
                        .map((descriptor) -> descriptor.getName().getLocalPart()).forEachOrdered((propName) -> {
                    sfb.set(propName, ftr.getProperty(propName).getValue());
                });
                sfb.set(GEOM_COLUMN_NAME, sf.getDefaultGeometry());
                featureList.add(sfb.buildFeature(null));
            }, null);

            return DataUtilities.collection(featureList);
        } catch (IOException ex) {
            Logger.getLogger(FeatureFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SimpleFeatureCollection getKmlFeatureCollection(FeatureSource fs) {
        try {
            SimpleFeatureType kmlFeatureType = getKmlFeatureType(fs);

            Collection<PropertyDescriptor> descriptors = fs.getSchema().getDescriptors();
            String geomName = fs.getSchema().getGeometryDescriptor().getLocalName();
            List<SimpleFeature> sfList = new ArrayList<>();

            fs.getFeatures().accepts(new FeatureVisitor() {
                @Override
                public void visit(Feature ftr) {
                    SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(kmlFeatureType);
                    for (PropertyDescriptor descriptor : descriptors) {
                        String descName = descriptor.getName().getLocalPart();
                        if (!descName.equals(geomName)) {
                            sfb.set(descName, ftr.getProperty(descName).getValue());
                        }
                    }

                    Class<?> geomBinding = kmlFeatureType.getGeometryDescriptor().getType().getBinding();
                    String newGeomName = kmlFeatureType.getGeometryDescriptor().getLocalName();
                    if (geomBinding.equals(Point.class)) {
                        List<Point> points = PointExtracter.getPoints((Geometry) ftr.getDefaultGeometryProperty().getValue());
                        for (Point point : points) {
                            sfb.set(newGeomName, point);
                            sfList.add(sfb.buildFeature(null));
                        }
                    } else if (geomBinding.equals(LineString.class)) {
                        List<LineString> lines = LineStringExtracter.getLines((Geometry) ftr.getDefaultGeometryProperty().getValue());
                        for (LineString line : lines) {
                            sfb.set(newGeomName, line);
                            sfList.add(sfb.buildFeature(null));
                        }
                    } else if (geomBinding.equals(Polygon.class)) {
                        List<Polygon> polygons = PolygonExtracter.getPolygons((Geometry) ftr.getDefaultGeometryProperty().getValue());
                        for (Polygon polygon : polygons) {
                            sfb.set(newGeomName, polygon);
                            sfList.add(sfb.buildFeature(null));
                        }
                    } else {
                        sfb.set(newGeomName, ftr.getDefaultGeometryProperty().getValue());
                        sfList.add(sfb.buildFeature(null));
                    }

                }
            }, null);

            return DataUtilities.collection(sfList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleFeatureType getKmlFeatureType(FeatureSource fs) {
        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        SimpleFeatureType schema = (SimpleFeatureType) fs.getSchema();
        sftb.setName(fs.getSchema().getName().getLocalPart());
        sftb.setDefaultGeometry(GEOM_COLUMN_NAME);
        if (fs.getSchema().getCoordinateReferenceSystem() == null) {
            sftb.setCRS(DEFAULT_CRS);
        } else {
            sftb.setCRS(fs.getSchema().getCoordinateReferenceSystem());
        }

        Class<?> geomBinding = schema.getGeometryDescriptor().getType().getBinding();
        if (geomBinding.equals(Geometry.class)) {
            geomBinding = specifyGeoemtryTypeFromFisrtGeometry(fs);
        }

        if (geomBinding.equals(MultiPoint.class)) {
            geomBinding = Point.class;
        } else if (geomBinding.equals(MultiLineString.class)) {
            geomBinding = LineString.class;
        } else if (geomBinding.equals(MultiPolygon.class)) {
            geomBinding = Polygon.class;
        }

        String geomColumnName = schema.getGeometryDescriptor().getLocalName();
        List<AttributeDescriptor> attributeDescriptors = schema.getAttributeDescriptors();
        attributeDescriptors.forEach((attributeDescriptor) -> {
            String attributeName = attributeDescriptor.getLocalName();
            if (!attributeName.equals(geomColumnName)) {
                sftb.add(attributeName, attributeDescriptor.getType().getBinding());
            }
        });
        sftb.add(GEOM_COLUMN_NAME, geomBinding);

        return sftb.buildFeatureType();
    }

    public SimpleFeatureCollection getSingleGeomColumnSFC(FeatureSource fs) {
        try {
            SimpleFeatureType geoJsonFeatureType = getSingleGeomColumnFeatureType(fs);
            List<SimpleFeature> sfList = new ArrayList<>();

            List<AttributeDescriptor> attributeDescriptors = geoJsonFeatureType.getAttributeDescriptors();
            fs.getFeatures().accepts((Feature ftr) -> {
                SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(geoJsonFeatureType);
                attributeDescriptors.forEach((attributeDescriptor) -> {
                    sfb.set(attributeDescriptor.getLocalName(), ftr.getProperty(attributeDescriptor.getLocalName()).getValue());
                });
                sfList.add(sfb.buildFeature(null));
            }, null);

            return DataUtilities.collection(sfList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public SimpleFeatureType getSingleGeomColumnFeatureType(FeatureSource fs) {

        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        SimpleFeatureType schema = fixNullCRSWithWGS84((SimpleFeatureType) fs.getSchema());
        sftb.setName(schema.getName().getLocalPart());
        if (schema.getCoordinateReferenceSystem() == null) {
            sftb.setCRS(DEFAULT_CRS);
        } else {
            sftb.setCRS(schema.getCoordinateReferenceSystem());
        }

        String geomColumnName = schema.getGeometryDescriptor().getLocalName();
        List<AttributeDescriptor> attributeDescriptors = schema.getAttributeDescriptors();
        attributeDescriptors.forEach((attributeDescriptor) -> {
            String attributeName = attributeDescriptor.getLocalName();
            if (!attributeName.equals(geomColumnName) && !Geometry.class.isAssignableFrom(attributeDescriptor.getType().getBinding())) {
                sftb.add(attributeName, attributeDescriptor.getType().getBinding());
            }
        });
        sftb.add(geomColumnName, schema.getGeometryDescriptor().getType().getBinding());

        return sftb.buildFeatureType();
    }

    public SimpleFeatureType fixNullCRSWithWGS84(SimpleFeatureType sft) {
        if (sft.getCoordinateReferenceSystem() == null) {
            SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
            //sftb.init(sft);
            sftb.setName(sft.getName());
            sftb.setCRS(DEFAULT_CRS);
            sftb.setDefaultGeometry(sft.getGeometryDescriptor().getLocalName());
            List<AttributeDescriptor> attributeDescriptors = sft.getAttributeDescriptors();
            for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
                sftb.add(attributeDescriptor.getLocalName(), attributeDescriptor.getType().getBinding());
            }

            return sftb.buildFeatureType();
        } else {
            return sft;
        }
    }

    public Class<?> specifyGeoemtryTypeFromFisrtGeometry(FeatureSource fs) {
        try {
            Query q = new Query();
            q.setMaxFeatures(1);
            SimpleFeature sf = (SimpleFeature) fs.getFeatures(q).toArray()[0];
            return sf.getDefaultGeometry().getClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
