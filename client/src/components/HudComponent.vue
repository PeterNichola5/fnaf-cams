<template>
    <div>
        <FnafButton id="cam01" name="CAM
        01" :cam="this.$store.state.hostProperties.cameras[0]"/>
        <FnafButton id="cam02" name="CAM
        02" :cam="this.$store.state.hostProperties.cameras[1]"/>
        <FnafButton id="cam03" name="CAM
        03" :cam="this.$store.state.hostProperties.cameras[2]"/>
        <FnafButton id="cam04" name="CAM
        04" :cam="this.$store.state.hostProperties.cameras[3]"/>
    </div>
</template>

<script>
import FnafButton from './FnafButton.vue';

export default {
    components: {
        FnafButton
    }
}

</script>

<style scoped>
    div{
        background-image: url("/public/Apt-fnaf-layout_PLAIN.png");
        background-size: contain;
        background-position: bottom;
        background-repeat: no-repeat;
        height: 380px;
        width: 600px;
    }

    #cam01 {
        position: relative;
        top: 330px;
        left: 525px;
    }

    #cam02 {
        position: relative;
        top: 205px;
        left: 260px;
    }

    #cam03 {
        position: relative;
        top: 180px;
        left: 80px;
    }

    #cam04 {
        position: relative;
        top: 20px;
        left: 510px;
    }
</style>