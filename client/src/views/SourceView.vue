<template>
    <div :class="{watched: isFocused}" >{{ focus }}</div>
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
</style>