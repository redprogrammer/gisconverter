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
package net.redelsoft.gisconverter;

/**
 * GIS Converter GUI Settings Parameters
 * 
 * @author Emre Demir
 */
public interface GUISettings {
 
    public static final String APP_TITLE = "GIS CONVERTER";
    public static final String FONT_NAME = "verdana";
    
    public static final String ALERT_ERROR_HEADER = "Converter Error";
    public static final String ALERT_ERROR_MESSAGE = "Error while converting process";
    
    public static final String ALERT_SUCCESS_HEADER = "Converter Success";
    public static final String ALERT_SUCCESS_MESSAGE = "The file converted successfully.";    
}
