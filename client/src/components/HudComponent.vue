<template>
    <div>
        <FnafButton id="cam01" name="CAM 01" @cam-viewed="changeFocus" :cam="this.$store.state.hostProperties.cameras[0]" :isDisabled="cam1Disabled"/>
        <FnafButton id="cam02" name="CAM 02" @cam-viewed="changeFocus" :cam="this.$store.state.hostProperties.cameras[1]" :isDisabled="cam2Disabled"/>
        <FnafButton id="cam03" name="CAM 03" @cam-viewed="changeFocus" :cam="this.$store.state.hostProperties.cameras[2]" :isDisabled="cam3Disabled"/>
        <FnafButton id="cam04" name="CAM 04" @cam-viewed="changeFocus" :cam="this.$store.state.hostProperties.cameras[3]" :isDisabled="cam4Disabled"/>
    </div>
</template>

<script>
import FnafButton from './FnafButton.vue';

export default {
    data() {
        return {
            currentFocus: null,
            cam1Disabled: false,
            cam2Disabled: false,
            cam3Disabled: false,
            cam4Disabled: false
        }
    },
    methods: {
        changeFocus(camName) {
            const camNum = camName.substring(camName.length - 1);
            if(this.currentFocus !== null) {
                switch(this.currentFocus) {
                    case '1':
                        this.cam1Disabled = true;
                    break;
                    case '2': 
                        this.cam2Disabled = true;
                    break;
                    case '3': 
                        this.cam3Disabled = true;
                    break;
                    case '4':
                        this.cam4Disabled = true;
                    break;
                    default:
                        console.log(`Illegal camNum: ${camNum}`);
                    break;
                }
                this.setDisabledTimeout(this.currentFocus);
            }
            this.currentFocus = camNum;  
        },
        setDisabledTimeout(camNum) {
            console.log(camNum);
            setTimeout(() => {
                console.log(camNum)
                switch(camNum) {
                    case '1':
                    this.cam1Disabled = false;
                    break;
                case '2': 
                    this.cam2Disabled = false;
                    break;
                case '3': 
                    this.cam3Disabled = false;
                    break;
                case '4':
                    this.cam4Disabled = false;
                    break;
                default:
                    console.log(`Illegal camNum: ${camNum}`);
                break;
                }
            }, 3000);
        }
    },
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
        left: 510px;
    }

    #cam02 {
        position: relative;
        top: 175px;
        left: 260px;
    }

    #cam03 {
        position: relative;
        top: 120px;
        left: 60px;
    }

    #cam04 {
        position: relative;
        top: -80px;
        left: 500px;
    }
</style>