package com.decoder.util;

public class PCMA extends G711Base {
    public static void alaw2linear(byte[] alaw, short[] lin, int frames) {
        for (int i = 0; i < frames; i++) {
            lin[i] = a2s[(alaw[i] & 0xFF)];
        }
    }

    public static byte[] alaw2linear(byte[] alaw, int frames) {
        byte[] retArr = new byte[frames * 2];
        boolean retArrPos = false;
        for (int i = 0; i < frames; i++) {
            short linTmp = a2s[(alaw[i] & 0xFF)];
            int var6 = i * 2;
            retArr[var6] = ((byte) (linTmp & 0xFF));
            retArr[(var6 + 1)] = ((byte) (linTmp >> 8 & 0xFF));
        }
        return retArr;
    }

    public static void alaw2linear(byte[] alaw, short[] lin, int frames, int mu) {
        for (int i = 0; i < frames; i++) {
            lin[i] = a2s[(alaw[(i / mu)] & 0xFF)];
        }
    }

    public static void linear2alaw(short[] lin, int offset, byte[] alaw, int frames) {
        for (int i = 0; i < frames; i++) {
            alaw[i] = s2a[(lin[(i + offset)] & 0xFFFF)];
        }
    }
}