<template>
    <div>
        <video  ref="video" :srcObject="currentStream" autoplay></video>
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
    video {
        width: 100%;
        height: 100%;
        z-index: -1;
    }

    #hud {
        position: fixed;
        z-index: 4;
        top: -300;
    }
</style>