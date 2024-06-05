<template>
    <div>
        <p>{{ this.$store.state.time }}</p>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                intervalId: null,
            }
        },
        mounted() {
            this.intervalId = setInterval(() => {
                switch(this.$store.state.time) {
                    case '12 AM':
                        this.$store.commit('SET_TIME', '1 AM');
                    break;
                    case '1 AM':
                        this.$store.commit('SET_TIME', '2 AM');
                    break;
                    case '2 AM':
                        this.$store.commit('SET_TIME', '3 AM');
                    break;
                    case '3 AM':
                        this.$store.commit('SET_TIME', '4 AM');
                    break;
                    case '4 AM':
                        this.$store.commit('SET_TIME', '5 AM');
                    break;
                    case '5 AM':
                        this.$store.commit('SET_GAME_STATE', 'win');
                        clearInterval(this.intervalId);
                    break;
                    default:
                        console.log(`state not recognized: ${this.$store.state.time}`);
                    break;
                }

            }, 1000);
        }
    };
</script>
