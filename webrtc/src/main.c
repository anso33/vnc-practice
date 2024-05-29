#include <rfb/rfbclient.h>
#include <gst/gst.h>

GstElement *pipeline, *appsrc, *videoconvert, *encoder, *muxer, *sink;

void initialize_gstreamer() {
    gst_init(NULL, NULL);

    pipeline = gst_pipeline_new("pipeline");
    appsrc = gst_element_factory_make("appsrc", "source");
    videoconvert = gst_element_factory_make("videoconvert", "videoconvert");
    encoder = gst_element_factory_make("x264enc", "encoder");
    muxer = gst_element_factory_make("mp4mux", "muxer");
    sink = gst_element_factory_make("filesink", "sink");

    g_object_set(sink, "location", "output.mp4", NULL);
    g_object_set(appsrc, "caps",
        gst_caps_new_simple("video/x-raw",
                            "format", G_TYPE_STRING, "RGB",
                            "width", G_TYPE_INT, 800,
                            "height", G_TYPE_INT, 600,
                            "framerate", GST_TYPE_FRACTION, 25, 1,
                            NULL), NULL);

    gst_bin_add_many(GST_BIN(pipeline), appsrc, videoconvert, encoder, muxer, sink, NULL);
    gst_element_link_many(appsrc, videoconvert, encoder, muxer, sink, NULL);
    gst_element_set_state(pipeline, GST_STATE_PLAYING);
}

void framebuffer_update(rfbClient* client, int x, int y, int w, int h) {
    GstBuffer *buffer;
    GstFlowReturn ret;
    int size = w * h * 4; // 4 bytes per pixel for 32bpp

    buffer = gst_buffer_new_allocate(NULL, size, NULL);
    gst_buffer_fill(buffer, 0, client->frameBuffer + (y * client->width + x) * 4, size);
    g_signal_emit_by_name(appsrc, "push-buffer", buffer, &ret);
    gst_buffer_unref(buffer);
}

int main(int argc, char** argv) {
    rfbClient* client = rfbGetClient(8, 3, 4);
    client->serverHost = "localhost"; // TightVNC 서버가 로컬호스트에서 실행 중인 경우
    client->serverPort = 5900; // TightVNC 서버의 포트 (디스플레이 번호가 1인 경우)
    client->GotFrameBufferUpdate = framebuffer_update;

    initialize_gstreamer();

    if (!rfbInitClient(client, NULL, NULL)) {
        return 1;
    }

    while (rfbClientIterate(client, 100000)) {
        // VNC 서버에서 프레임버퍼 업데이트를 수신하여 GStreamer로 전송
    }

    rfbClientCleanup(client);
    gst_element_set_state(pipeline, GST_STATE_NULL);
    gst_object_unref(pipeline);
    return 0;
}
