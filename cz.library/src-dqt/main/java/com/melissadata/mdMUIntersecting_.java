/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.melissadata;

public class mdMUIntersecting_ extends mdMUBatchDedupe_ {
  private long swigCPtr;

  protected mdMUIntersecting_(long cPtr, boolean cMemoryOwn) {
    super(mdMatchUpJavaWrapperJNI.mdMUIntersecting__SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(mdMUIntersecting_ obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        mdMatchUpJavaWrapperJNI.delete_mdMUIntersecting_(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public mdMUIntersecting_() {
    this(mdMatchUpJavaWrapperJNI.new_mdMUIntersecting_(), true);
  }

}
