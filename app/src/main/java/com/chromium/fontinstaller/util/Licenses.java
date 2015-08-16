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

package com.chromium.fontinstaller.util;

import java.util.Arrays;
import java.util.List;

import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class Licenses {

    private static Notices notices = new Notices();

    public static final List<Notice> NOTICE_LIST = Arrays.asList(
            new Notice(
                    "OkHttp",
                    "https://github.com/square/okhttp",
                    "Copyright 2012 Square, Inc.",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "libsuperuser",
                    "https://github.com/Chainfire/libsuperuser",
                    "Copyright (C) 2012-2014 Jorrit \"Chainfire\" Jongma",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "RxJava",
                    "https://github.com/Netflix/RxJava",
                    "Copyright 2014 Netflix, Inc.",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "ButterKnife",
                    "https://github.com/JakeWharton/butterknife",
                    "Copyright 2013 Jake Wharton",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "Timber",
                    "https://github.com/JakeWharton/timber",
                    "Copyright 2013 Jake Wharton",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "Android Support Library",
                    "http://developer.android.com/tools/support-library/",
                    "Copyright (C) 2011 The Android Open Source Project",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "Google Play Services",
                    "https://developer.android.com/google/play-services/",
                    "Copyright (C) 2011 The Android Open Source Project",
                    new ApacheSoftwareLicense20()
            ),
            new Notice(
                    "Floating Action Button",
                    "https://github.com/makovkastar/FloatingActionButton",
                    "Copyright (c) 2014 Oleksandr Melnykov\n",
                    new MITLicense()
            ),
            new Notice(
                    "RecyclerView StickyHeaders",
                    "https://github.com/eowise/recyclerview-stickyheaders",
                    "Copyright (c) 2014",
                    new MITLicense()
            ),
            new Notice(
                    "Snackbar",
                    "https://github.com/nispok/snackbar",
                    "Copyright (c) 2015 William Mora\n",
                    new MITLicense()
            ),
            new Notice(
                    "LicensesDialog",
                    "http://psdev.de",
                    "Copyright 2013 Philip Schiffer <admin@psdev.de>",
                    new ApacheSoftwareLicense20()
            )
    );

    public static Notices getNotices() {
        for (Notice notice : NOTICE_LIST) notices.addNotice(notice);
        return notices;
    }
}
