#include <jni.h>
#include <unistd.h>

JNIEXPORT jint JNICALL
Java_mobile_xiyou_atest_JniTest_j_1fork(JNIEnv *env, jclass type) {

    // TODO

    int r=0;
    if ((r=fork())==0)
    {
        setsid();
        r=fork();

        if (r!=0)
        {
            _exit(0);
        }
    }

    return r;
}