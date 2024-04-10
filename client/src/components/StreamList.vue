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