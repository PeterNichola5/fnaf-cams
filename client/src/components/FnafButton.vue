<template>
    <main>
        <button ref="button" :class="{noSrc: !hasValidSrc, focused: isfocused}" @click="changeCam">{{ label }}</button>
    </main>
</template>

<script>
    import {ref} from 'vue';
    export default {
        setup() {
            const button = ref(null);
            return {
                button
            }
        },
        data() {
            return {
                isfocused: false,
            }
        },
        props: ['cam', 'name', 'isDisabled'],
        computed: {
            hasValidSrc() {
                return this.cam.src !== null;
            },
            label() {
                const nameParts = this.name.split(" ");
                return this.isDisabled? 'ERROR' : `${nameParts[0]}\n${nameParts[1]}`;
            }
        },
        methods: {
            changeCam() {
                if(!this.isfocused && this.hasValidSrc) {
                    this.$emit('cam-viewed', this.name);
                    console.log(`${this.name} pressed`);
                    this.isfocused = true;
                    this.$store.commit('SET_CURRENT_STREAM', this.cam);
                }
                
            }
        },
        watch: {
            isDisabled: function(newVal) {
                this.button.disabled = newVal;

                if(newVal) {
                    this.isfocused = false;
                }
            }
        },
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

    button:disabled {
        color:#b8b8b8;
        cursor: not-allowed;
        background-color: #2b0404;
        border-color: #b8b8b8;
        text-align: center;
    }

    .noSrc {
        background: red;
        cursor: not-allowed;
    }

    .focused {
        background: green;
    }
</style>