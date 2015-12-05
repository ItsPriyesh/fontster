/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chromium.fontinstaller.models;

import org.junit.Test;

import java.io.File;

import static com.chromium.fontinstaller.TestUtils.getFileResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FontPackageTest {

    private static final String FONT_NAME = "Aleo";

    private static final String INVALID_FONT_FOLDER_NAME = "InvalidFontPack";
    private static final String VALID_FONT_FOLDER_NAME = "ValidFontPack";

    @Test public void testCreateFontPack_hasAllStyles() throws Exception {
        final FontPackage fontPackage = new FontPackage(FONT_NAME);
        assertEquals(Style.values().length, fontPackage.getFontList().size());
    }

    @Test public void testValidFontPackFolder_returnsFalseIfInvalid() throws Exception {
        final File invalidFontPackFolder = getFileResource(INVALID_FONT_FOLDER_NAME);
        assertFalse(FontPackage.validFontPackFolder(invalidFontPackFolder.getPath()));
    }

    @Test public void testValidFontPackFolder_returnsTrueIfValid() throws Exception {
        final File validFontPackFolder = getFileResource(VALID_FONT_FOLDER_NAME);
        assertTrue(FontPackage.validFontPackFolder(validFontPackFolder.getPath()));
    }
}