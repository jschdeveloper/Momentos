package com.jschdev.momentos.Video;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by tcsadmin on 06/11/2015.
 */
public class SequenceEncoder {
    private SeekableByteChannel ch;
    private Picture toEncode;
    private Transform transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;

    public SequenceEncoder(File out, int fps) throws IOException {
        this.ch = NIOUtils.writableFileChannel(out);
        this.muxer = new MP4Muxer(this.ch, Brand.MP4);
        //this.outTrack = this.muxer.addTrack(TrackType.VIDEO, 25);
        this.outTrack = muxer.addTrack(TrackType.VIDEO, fps);
        this._out = ByteBuffer.allocate(12441600);
        //this._out = ByteBuffer.allocate(640*360);
        this.encoder = new H264Encoder();
        this.transform = ColorUtil.getTransform(ColorSpace.RGB, this.encoder.getSupportedColorSpaces()[0]);
        this.spsList = new ArrayList();
        this.ppsList = new ArrayList();
    }

    public void encodeNativeFrame(Picture pic, int fps) throws IOException {
        if (this.toEncode == null) {
            this.toEncode = Picture.create(pic.getWidth(), pic.getHeight(), this.encoder.getSupportedColorSpaces()[0]);
        }

        this.transform.transform(pic, this.toEncode);
        this._out.clear();
        ByteBuffer result = this.encoder.encodeFrame(this.toEncode, this._out);
        this.spsList.clear();
        this.ppsList.clear();
        H264Utils.wipePS(result, this.spsList, this.ppsList);
        H264Utils.encodeMOVPacket(result);
        //this.outTrack.addFrame(new MP4Packet(result, (long) this.frameNo, 1L, 1L, (long) this.frameNo, true, (TapeTimecode) null, (long) this.frameNo, 0));
        //this.outTrack.addFrame(new MP4Packet(result, frameNo * 1, (int) FPS, 1, frameNo, true, null, frameNo * 1, 0));
        this.outTrack.addFrame(new MP4Packet(result, frameNo * 1, fps, fps, frameNo, true, null, frameNo * 1, 0));

        ++this.frameNo;
    }

    public void finish() throws IOException {
        this.outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(this.spsList, this.ppsList, 4));
        this.muxer.writeHeader();

        NIOUtils.closeQuietly(this.ch);
    }
}
