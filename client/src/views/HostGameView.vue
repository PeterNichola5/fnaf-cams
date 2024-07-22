<template>
    <div v-if="this.$store.state.gameWon" id="winner">WIN!!!</div>
    <div id="guard-container">
        <ClockComponent id="clock"/>
        <video  id="source" ref="stream" :srcObject="currentStream" autoplay></video>
        <video  id="overlay" ref="video" :class="{show: this.longView}" src="../assets/viewtoolong.mp4"></video>
        <div id="hud-container"></div>
        <HudComponent id="hud"/>
    </div> 
</template>

<script>
    import HudComponent from '../components/HudComponent.vue';
    import ClockComponent from '../components/ClockComponent.vue';
    import {ref} from "vue";

    export default {
        setup() {
            const video = ref(null);
            return {
                video
            }
        },
        data() {
            return {
                timeoutId: null,
                longView: false
            }
        },
        components: {
            HudComponent,
            ClockComponent
        },

        mounted() {
            this.beginTimeout();
        },

        methods: {
            beginTimeout() {
                this.video.pause();
                this.video.load();
                this.timeoutId = setTimeout(() => {
                    this.longView = true;
                    this.video.play().then(() => {});
                }, 5000);
            },
        },

        computed: {
            currentStream() {
                return this.$store.state.hostProperties.currentCam === null ? null : this.$store.state.hostProperties.currentCam.src.stream;
            },
            currentTime() {
                return this.$store.state.time;
            },
            gameStatus() {
                if(this.$store.state.gameWon) {
                    return 'WIN';
                }
                if(this.$store.state.gameLost) {
                    return 'LOST';
                }
                return 'IN_PROGRESS';
            }
        },
        watch: {
            currentTime(val) {
                for(let camera of this.$store.state.hostProperties.cameras) {
                    camera.src.messages.send(`TIME=${val}`);
                }
            },
            gameStatus(val) {
                for(let camera of this.$store.state.hostProperties.cameras) {
                    camera.src.messages.send(val);
                }
            },
            currentStream() {
                clearTimeout(this.timeoutId);
                this.longView = false;
                this.beginTimeout();
            }
        },
    }
</script>

<style scoped>
    #guard-container {
        bottom: 10px;
        right: 10px;
    }
    #source {
        position: absolute;
        width: 100vw;
        height: 100vh;
        object-fit: fill;
        z-index: -1;
        filter: grayscale();
    }

    #overlay {
        position: absolute;
        min-width: 100%;
        min-height: 100%;
        max-height: 100%;
        width:100vw;
        z-index: 1;
        opacity: 0;
        object-fit: fill;
    }

    .show {
        opacity: 1 !important;
        transition: opacity 2s;
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