/* Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */


package com.aws.iot.evergreen.util;

import java.io.*;
import java.nio.file.*;

public class CommitableWriter extends BufferedWriter implements Commitable {
    private final CommitableFile out;
    private boolean open = true;
    private CommitableWriter(CommitableFile f) {
        super(new OutputStreamWriter(new BufferedOutputStream(f)));
        out = f;
        
    }
    /** Strangely enough, abandonOnClose is usually the best choice: it interacts
     *  well with the implicit close() that happens in a try-with-resources where
     *  files are closed if an exception is tossed */
    public static CommitableWriter abandonOnClose(Path p) throws IOException {
        return new CommitableWriter(CommitableFile.abandonOnClose(p));
    }
    public static CommitableWriter commitOnClose(Path p) throws IOException {
        return new CommitableWriter(CommitableFile.commitOnClose(p));
    }
    @Override
    public void commit() {
        if(open) {
            try {
                flush();
            } catch (IOException ex) { }
            out.commit();
            open = false;
        }
    }
    @Override
    public void abandon() {
        if(open) {
            out.abandon();
            open = false;
        }
    }

}