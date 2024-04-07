<template>
    <div id="guard-container">
        <video  ref="video" :srcObject="currentStream" autoplay></video>
        <div id="hud-container"></div>
        <HudComponent id="hud"/>
        
    </div>
    
</template>

<script>
    import HudComponent from '../components/HudComponent.vue';

    export default {
        components: {
            HudComponent
        },
        computed: {
            currentStream() {
                console.log(this.$store.state.hostProperties.currentCam);
                return this.$store.state.hostProperties.currentCam === null ? null : this.$store.state.hostProperties.currentCam.src.stream;
            }
        }
    }
</script>

<style scoped>
    #guard-container {
        bottom: 10px;
        right: 10px;
    }
    video {
        position: absolute;
        min-width: 100%;
        min-height: 100%;
        z-index: -1;
        filter: grayscale();
    }

    #hud-container {
        z-index: 3;
        position: fixed;
        width: 600px;
        height: 380px;
        background-color: rgba(0, 0, 0, 0.5);
        border-radius: 0.5rem;
        bottom: 10px;
        right: 10px;
        
    }
    #hud {
        position: fixed;
        z-index: 4;
        bottom: 10px;
        right: 0px;
    }
</style>