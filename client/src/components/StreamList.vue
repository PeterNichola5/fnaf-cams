<template>
    <ul>
        <li v-for="connection in this.$store.state.hostProperties.connections" :key="connection.id">
        <video  ref="video" :srcObject="connection.stream" autoplay></video>
        </li>
    </ul>
</template>

<script>
</script>

<style scoped>
    ul {
        display: flex;
        flex-wrap: wrap;
        list-style-type: none;
        max-height: 300px;
        overflow-y: scroll;
    }

    ul::-webkit-scrollbar {
        width: 1rem;
        padding-right: 0.2rem;
        background-color: grey;
        border-radius: 3rem;
    }

    li {
        padding: 10px;
    }

    video {
        height: 200px;
        filter: grayscale();
        border: 5px grey solid;
    }
</style>