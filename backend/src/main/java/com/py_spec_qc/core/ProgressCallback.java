package com.py_spec_qc.core;

import com.py_spec_qc.core.model.FileProgress;

@FunctionalInterface
public interface ProgressCallback {
    void onUpdate(FileProgress update);
}

