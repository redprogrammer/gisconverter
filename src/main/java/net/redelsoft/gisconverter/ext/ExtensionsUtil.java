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
package net.redelsoft.gisconverter.ext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.redelsoft.gisconverter.core.type.CsvFile;
import net.redelsoft.gisconverter.core.type.GeoJsonFile;
import net.redelsoft.gisconverter.core.type.GmlFile;
import net.redelsoft.gisconverter.core.type.KmlFile;
import net.redelsoft.gisconverter.core.type.ShapeFile;

/**
 *
 * @author Emre Demir
 */
public class ExtensionsUtil {

    private final List<FileExtension> extensionList = Arrays.asList(
            new FileExtension("ESRI Shape File (.shp)", "shp", new ShapeFile()),
            new FileExtension("GeoJson (.geojson)", "geojson", new GeoJsonFile()),
            new FileExtension("CSV", "csv", new CsvFile()),
            new FileExtension("KML", "kml", new KmlFile()),
            new FileExtension("GML", "gml", new GmlFile())
    );

    public List<String> getExtensionsFileChooser() {
        return extensionList.stream().map(s -> ("*." + s.getExtension()))
                .collect(Collectors.toList());
    }

    public List<FileExtension> getExtensions(String filter) {
        return extensionList.stream()
                .map(s -> s)
                .filter(s -> !s.getExtension().equals(filter))
                .collect(Collectors.toList());
    }

    public FileExtension getFileExtension(String ext) {
        return extensionList.stream()
                .map(s -> s)
                .filter(s -> s.getExtension().equals(ext))
                .findFirst()
                .get();
    }

}
