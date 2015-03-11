// Setup
CudaToolkitIncludeDir
cuda.lib
// find these using FindPackage(CUDA)

NvHWEncoder.cpp,.h from samples/common
// will take care of setting up api

int CNvEncoder::EncodeMain(int argc, char *argv[])
{
    EncodeConfig // Contains config for encoder
    // we can skip HWEncoder->parseArguments, they just override what is in
    // ENcode Config
    
    InitCuda(uint32_t deviceID)
    {
        // Create/check cuda device & context
    }
    CNvHWEncoder::Initialize(void* device, NV_ENC_DEVICE_TYPE deviceType)
    {
        // Loads dynamic library, initializes API, opens encode session
    }
    
    // Can skip HWEncoder::GetPresetGUID, just use NV_ENC_PRESET_LOW_LATENCY_HP_GUID
    
    CNvHWEncoder::ValidatePresetGUID(GUID inputPresetGuid, GUID inputCodecGuid)
    {
        // Make sure we can use preset with codec. Uses enumeration of supported
        // codecs, preset guids etc
    }
    
    CNvHWEncoder::CreateEncoder(const EncodeConfig *pEncCfg)
    {
        // Sets up all encoder parameters, validates them and does 
        // nvEnvInitializeEncoder.
    }
    
    // AllocateIOBuffers -> needs to change to use CUDA interop.
    // first try: just use this.
    AllocateIOBuffers(uint32_t uInputWidth, uint32_t uInputHeight, uint32_t isYuv444)
}

////////////////////////////////////////////////////////////////////////////////
class CameraStreamer: public ICameraListener
{
    initGraphics()
    {
        // create render target
        // create & attach textures
        // create encoder
        // pass textures to encoder. It will take care of binding to CUDA mem.
    }
    beginFrame()
    {
        if(!initialized) initializeGraphics();
        
    }
    endFrame()
    {
        Encoder->encodeFrame();
    }
    beginDraw()
    {
        // bind render target
    }
    endDraw()
    {
        // unbind render target
    }
    
    lock/unlock bitstream (just wrappers around encoder)
    
private:
    Encoder* e;
}

class Encoder
{
    initialize(...);
    setSource(Texture* t);
    encodeFrame(...?);
    lockBitstream / unlockBitstream (see below)
    
    queueFrame()
    
}

// Modify NvHWEncoder::ProcessOutput:
NVENCSTATUS CNvHWEncoder::LockBitstream(const EncodeBuffer *pEncodeBuffer, void** bitstreamptr, uint32_t* bytes);
// add
NVENCSTATUS CNvHWEncoder::UnlockBitstream(const EncodeBuffer *pEncodeBuffer);
