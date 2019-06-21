#include <jni.h>
#include <string>
#include <sys/system_properties.h>
using namespace std;


string base64_encode(unsigned char const* , unsigned int len);
string base64_decode(string const& s);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_physson_getphys_Home_phyIn(JNIEnv *env, jobject thisObj,jstring t) {

    const char *str= env->GetStringUTFChars(t,0);
    //const jchar *nativestring = env->GetStringChars(t,0);
    string hello =str;
    string all="";
    for (int i =0;i<hello.length();i++ ){

        if (i==hello.length()-1){
            all =base64_encode(reinterpret_cast<const unsigned char*>(all.c_str()),all.length());

            all = all.substr(0,12)+hello.substr(hello.length()-1);
            all =base64_encode(reinterpret_cast<const unsigned char*>(all.c_str()),all.length());

        }else{

            all =hello.substr(i,1)+base64_encode(reinterpret_cast<const unsigned char*>(all.c_str()),all.length());

        }
    }

    //env->ReleaseStringUTFChars(t, str);

    return env->NewStringUTF(all.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_physson_getphys_Home_phyOut(JNIEnv *env, jobject /* this */,jstring t) {
    const char *str= env->GetStringUTFChars(t,0);
    const jchar *nativestring = env->GetStringChars(t,0);
    string hello =str;
    string rest0_decoded = base64_decode(hello);
    env->ReleaseStringUTFChars(t, str);

    return env->NewStringUTF(rest0_decoded.c_str());
}



static const string base64_chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                "abcdefghijklmnopqrstuvwxyz"
                "0123456789+/";


static inline bool is_base64(unsigned char c) {
    return (isalnum(c) || (c == '+') || (c == '/'));
}

string base64_encode(unsigned char const* bytes_to_encode, unsigned int in_len) {
    string ret;
    int i = 0;
    int j = 0;
    unsigned char char_array_3[3];
    unsigned char char_array_4[4];

    while (in_len--) {
        char_array_3[i++] = *(bytes_to_encode++);
        if (i == 3) {
            char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
            char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
            char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
            char_array_4[3] = char_array_3[2] & 0x3f;

            for(i = 0; (i <4) ; i++)
                ret += base64_chars[char_array_4[i]];
            i = 0;
        }
    }

    if (i)
    {
        for(j = i; j < 3; j++)
            char_array_3[j] = '\0';

        char_array_4[0] = ( char_array_3[0] & 0xfc) >> 2;
        char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
        char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);

        for (j = 0; (j < i + 1); j++)
            ret += base64_chars[char_array_4[j]];

        while((i++ < 3))
            ret += '=';

    }

    return ret;

}

string base64_decode(string const& encoded_string) {
    int in_len = encoded_string.size();
    int i = 0;
    int j = 0;
    int in_ = 0;
    unsigned char char_array_4[4], char_array_3[3];
    string ret;

    while (in_len-- && ( encoded_string[in_] != '=') && is_base64(encoded_string[in_])) {
        char_array_4[i++] = encoded_string[in_]; in_++;
        if (i ==4) {
            for (i = 0; i <4; i++)
                char_array_4[i] = base64_chars.find(char_array_4[i]);

            char_array_3[0] = ( char_array_4[0] << 2       ) + ((char_array_4[1] & 0x30) >> 4);
            char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);
            char_array_3[2] = ((char_array_4[2] & 0x3) << 6) +   char_array_4[3];

            for (i = 0; (i < 3); i++)
                ret += char_array_3[i];
            i = 0;
        }
    }

    if (i) {
        for (j = 0; j < i; j++)
            char_array_4[j] = base64_chars.find(char_array_4[j]);

        char_array_3[0] = (char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4);
        char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);

        for (j = 0; (j < i - 1); j++) ret += char_array_3[j];
    }

    return ret;
}
