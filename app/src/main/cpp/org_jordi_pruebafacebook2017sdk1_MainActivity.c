#include "org_jordi_pruebafacebook2017sdk1_MainActivity.h"
#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "libmainactivity"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t alpha;
} rgba; /*Conversion a grises por pixel*/

JNIEXPORT void JNICALL
Java_org_jordi_pruebafacebook2017sdk1_MainActivity_convertirGrises(JNIEnv *env, jobject obj,
                                                      jobject bitmapcolor, jobject bitmapgris) {
//Java_com_imgprocesadondk_ImgProcesadoNDK_convertirGrises(JNIEnv *env, jobject obj,
 //                                                        jobject bitmapcolor, jobject bitmapgris) {
    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infogris;
    void *pixelsgris;
    int ret;
    int y;
    int x;
    LOGI("convertirGrises");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmapgris, &infogris)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride, infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infogris.width,
         infogris.height, infogris.stride, infogris.format, infogris.flags);
    if (infogris.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapgris, &pixelsgris)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    } // modificacion pixeles en el algoritmo de escala grises

    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        rgba *grisline = (rgba *) pixelsgris;
        for (x = 0; x < infocolor.width; x++) {
            float output = (line[x].red + line[x].green + line[x].blue) / 3;
            if (output > 255) output = 255;
            grisline[x].red = grisline[x].green = grisline[x].blue = (uint8_t) output;
            grisline[x].alpha = line[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsgris = (char *) pixelsgris + infogris.stride;
    }
    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapgris);
}

JNIEXPORT void JNICALL
Java_org_jordi_pruebafacebook2017sdk1_MainActivity_convertirSepia(JNIEnv *env, jobject obj,
                                                       jobject bitmapcolor, jobject bitmapsepia) {
//Java_com_imgprocesadondk_ImgProcesadoNDK_convertirSepia(JNIEnv *env, jobject obj,
                                                     //   jobject bitmapcolor, jobject bitmapsepia) {
    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infosepia;
    void *pixelssepia;
    int ret;
    int y;
    int x;
    LOGI("convertirSepia");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmapsepia, &infosepia)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride, infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infosepia.width,
         infosepia.height, infosepia.stride, infosepia.format, infosepia.flags);
    if (infosepia.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapsepia, &pixelssepia)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    } // modificacion pixeles en el algoritmo de escala grises

    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        rgba *sepialine = (rgba *) pixelssepia;
        for (x = 0; x < infocolor.width; x++) {
            double redPrueba =
                    (line[x].red * .393) + (line[x].green * .769) + (line[x].blue * .189);
            double greenPrueba =
                    (line[x].red * .349) + (line[x].green * .686) + (line[x].blue * .168);
            double bluePrueba =
                    (line[x].red * .272) + (line[x].green * .534) + (line[x].blue * .131);

            if (redPrueba > 255) {
                sepialine[x].red = (uint8_t) 255;
            } else {
                sepialine[x].red = (uint8_t) redPrueba;
            }
            if (greenPrueba > 255) {
                sepialine[x].green = (uint8_t) 255;
            } else {
                sepialine[x].green = (uint8_t) greenPrueba;
            }

            if (bluePrueba > 255) {
                sepialine[x].blue = (uint8_t) 255;
            } else {
                sepialine[x].blue = (uint8_t) bluePrueba;
            }
            sepialine[x].alpha = line[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelssepia = (char *) pixelssepia + infosepia.stride;
    }
    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapsepia);
}

JNIEXPORT void JNICALL //Java_com_imgprocesadondk_ImgProcesadoNDK_creaMarco
          Java_org_jordi_pruebafacebook2017sdk1_MainActivity_creaMarco
        (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapmarco) {
    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infomarco;
    void *pixelsmarco;
    int ret;
    int y;
    int x;
    int limitalto;
    int limitdcha;

    LOGI("convertirMarco");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmapmarco, &infomarco)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride, infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infomarco.width,
         infomarco.height, infomarco.stride, infomarco.format, infomarco.flags);
    if (infomarco.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapmarco, &pixelsmarco)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    } // modificacion pixeles en el algoritmo de escala grises

    limitdcha = infocolor.width - 10;
    limitalto = infocolor.height - 10;
    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        rgba *marcoline = (rgba *) pixelsmarco;
        for (x = 0; x < infocolor.width; x++) {
            if (y <= 9 || y >= limitalto) {
                marcoline[x].red = marcoline[x].green = marcoline[x].blue = (uint8_t) 0;
            } else if (x <= 9 || x >= limitdcha) {
                marcoline[x].red = marcoline[x].green = marcoline[x].blue = (uint8_t) 0;
            } else {
                marcoline[x].red = line[x].red;
                marcoline[x].green = line[x].green;
                marcoline[x].blue = line[x].blue;
            }
            marcoline[x].alpha = line[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsmarco = (char *) pixelsmarco + infomarco.stride;
    }
    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapmarco);
}



/*JNIEXPORT  jboolean JNICALL Java_com_imgprocesadondk_ImgProcesadoNDK_callback
        (JNIEnv *env , jobject thiz) {
    // #define JNI_FALSE  0
    // #define JNI_TRUE   1
    jboolean prueba = JNI_FALSE;
    LOGI("haypixel llamada!");
    jclass clazz = (*env)->GetObjectClass(env, thiz);
    if (!clazz) {
        LOGE("callback_handler: FALLO object Class");
        goto failure;
    }
    jmethodID method = (*env)->GetStaticMethodID(env, clazz, "haypixel", "(II)Z");
    if (!method) {
        LOGE("callback_hand ler: FALLO metodo ID");
        goto failure;
    }
    prueba = (*env)->CallStaticByteMethod(env, thiz, method);
    failure:
    return prueba;
}*/


JNIEXPORT void JNICALL //Java_com_imgprocesadondk_ImgProcesadoNDK_creaMarcoCallBack
        //(JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapmarco) {
        Java_org_jordi_pruebafacebook2017sdk1_MainActivity_creaMarcoCallBack
                (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapmarco){
    jboolean prueba = JNI_FALSE;
    LOGI("haypixel llamada!");
    jclass clazz = (*env)->GetObjectClass(env, obj);
    if (!clazz) {
        LOGE("callback_handler: FALLO object Class");
        goto failure;
    }

    jmethodID method = (*env)->GetStaticMethodID(env, clazz, "hayPixel", "(II)Z");
    if (!method) {
        LOGE("callback_hand ler: FALLO metodo ID");
        goto failure;
    }

    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infomarco;
    void *pixelsmarco;
    int ret;
    int y;
    int x;
    int limitalto;
    int limitdcha;
    uint8_t color;

    LOGI("convertirMarco");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmapmarco, &infomarco)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride, infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infomarco.width,
         infomarco.height, infomarco.stride, infomarco.format, infomarco.flags);
    if (infomarco.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmapmarco, &pixelsmarco)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    } // modificacion pixeles en el algoritmo de escala grises

    limitdcha = infocolor.width - 10;
    limitalto = infocolor.height - 10;
    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        rgba *marcoline = (rgba *) pixelsmarco;
        for (x = 0; x < infocolor.width; x++) {
            prueba = (*env)->CallStaticBooleanMethod(env, clazz, method);
            if (prueba == JNI_TRUE) { color = 0; }
            else { color = 255; }
            if (y <= 9 || y >= limitalto) {
                marcoline[x].red = marcoline[x].green = marcoline[x].blue = color;
            } else if (x <= 9 || x >= limitdcha) {
                marcoline[x].red = marcoline[x].green = marcoline[x].blue = color;
            } else {
                marcoline[x].red = line[x].red;
                marcoline[x].green = line[x].green;
                marcoline[x].blue = line[x].blue;
            }

            marcoline[x].alpha = line[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsmarco = (char *) pixelsmarco + infomarco.stride;
    }
    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapmarco);
    failure:
    return;
}