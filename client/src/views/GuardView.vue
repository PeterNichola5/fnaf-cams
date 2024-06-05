<template>
    <div v-if="this.$store.state.gameWon" id="winner">WIN!!!</div>
    <div id="guard-container">
        <ClockComponent id="clock"/>
        <video  ref="video" :srcObject="currentStream" autoplay></video>
        <div id="hud-container"></div>
        <HudComponent id="hud"/>
    </div> 
</template>

<script>
    import HudComponent from '../components/HudComponent.vue';
    import ClockComponent from '../components/ClockComponent.vue';

    export default {
        data() {
            return {
            }
        },
        components: {
            HudComponent,
            ClockComponent
        },
        computed: {
            currentStream() {
                console.log(this.$store.state.hostProperties.currentCam);
                return this.$store.state.hostProperties.currentCam === null ? null : this.$store.state.hostProperties.currentCam.src.stream;
            },
            currentTime() {
                return this.$store.state.time;
            }
        },
        watch: {
            currentTime(val) {
                console.log('called');
                for(let camera of this.$store.state.hostProperties.cameras) {
                    camera.src.messages.send(`TIME=${val}`);
                }
            }
        },
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
    #clock {
        position: fixed;
        z-index: 4;
        font-family: "Press Start 2P";
        font-size: 3rem;
        color: #b9b9b9;
        padding: 1rem;
    }
    #winner {
        position: fixed;
        width: 100vw;
        height: 100vh;
        background-color: rgb(45, 147, 11);
        z-index: 5;
        font-family: "Press Start 2P";
        color: #fcfcfc;
    }
</style>