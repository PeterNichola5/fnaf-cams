<template>
    <main>
        <button :class="{noSrc: !hasValidSrc}" @click="changeCam">{{ name }}</button>
    </main>
</template>

<script>
    export default {
        data() {
            return {

            }
        },
        props: ['cam', 'name'],
        computed: {
            hasValidSrc() {
                return this.cam.src !== null;
            }
        },
        methods: {
            changeCam() {
                console.log('button pressed');
                this.$store.commit('SET_CURRENT_STREAM', this.cam);
            }
        }
    }

</script>

<style scoped>
    button {
        font-family: "Press Start 2P";
        color: #fcfcfc;
        font-size: 1.5vmin;
        text-align: left;
        border: 0.18rem solid #fcfcfc;
        padding: 0.625vmin;
        width: 3.7rem;
        height: 2rem;
        background: #333;
        display: inline-block;
        z-index: 100004;
        cursor: pointer;
    }

    main {
        max-width: max-content;
    }

    .noSrc {
        background: red;
    }
</style>