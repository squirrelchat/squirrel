/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.upload;

public enum FileType { // TODO: Add more types and add their magic bytes
    // Images
    JPG(null, "jpg"),
    PNG(null, "png"),
    GIF(null, "gif"),
    WEBP(null, "webp"),
    APNG(null, "png"),
    // Audio
    MP3(null, "mp3"),
    // Video
    MP4(null, "mp4");

    public static FileType[] images = {JPG, PNG, WEBP, GIF, APNG};
    public static FileType[] static_images = {JPG, PNG, WEBP};
    public static FileType[] animated_images = {GIF, APNG};
    public static FileType[] audio = {MP3};
    public static FileType[] video = {MP4};

    private final byte[] magicBytes;
    private final String extension;

    FileType(byte[] magicBytes, String extension) {
        this.magicBytes = magicBytes;
        this.extension = extension;
    }

    public byte[] getMagicBytes() {
        return magicBytes;
    }

    public String getExtension() {
        return extension;
    }
}
