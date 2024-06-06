<template>
    <div id="win-screen" v-if="this.$store.state.gameWon">SECURITY GUARD WINS</div>
    <div v-else :class="{watched: isFocused}" >
        <p id="time">{{ this.$store.state.time }}</p>
        <p>{{ focus }}</p>
    </div>
</template>

<script>

    export default {
        data() {
            return {
                focus: 'NOT BEING FOCUSED',
                isFocused: false
            }
        },

        mounted() {
           const connection = this.$store.state.srcConnection;
           console.log(connection);
           connection.messages.onmessage = event => {
                console.log(event.data);
                if(event.data.includes('ON')) {
                    this.focus = 'YOU ARE BEING WATCHED!!!!';
                    this.isFocused = true;
                } else if(event.data.includes('OFF')) {
                    this.focus = 'NOT BEING FOCUSED';
                    this.isFocused = false;
                } else if(event.data.includes('TIME')) {
                    console.log('updating time');
                    const newTime = event.data.split('TIME=');
                    this.$store.commit('SET_TIME', newTime[1]);
                } else if(event.data.includes('WIN')) {
                    this.$store.commit('SET_GAME_STATE', 'WIN');
                } else if(event.data.includes('LOST')) {
                    this.$store.commit('SET_GAME_STATE', 'LOST');
                }
           }
        }
    }
</script>

<style scoped>
    div {
        height: 100vh;
        background-color: black;
        color: white;
    }
    .watched{
        background-color: white;
        color: black;
    }
    #win-screen {
        width: 100vw;
        height: 100vh;
        background-color: aquamarine;
    }
    div, p {
        font-family: "Press Start 2P";
    }
</style>