<template>
    <video id="vhs-effect" autoplay loop>
        <source src="../assets/VHS Static.mp4" type="video/mp4"/>
    </video>
    <video id="audio" autoplay loop type="audio/mp3">
        <source src="../assets/fnaf-1-music-box.mp3"/>
    </video>
    <div id="backdrop">
        <h1>
            <span id="bottom">PETERNICHOLAS</span>
            <span id="middle">PETERNICHOLAS</span>
            <span id="top">PETERNICHOLAS</span>
        </h1>
    </div>
</template>

<script>
</script>

<style scoped>
    #backdrop {
        position: absolute;
        width: 100vw;
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    #audio {
        position: absolute;
        z-index: -5;
    }

    #vhs-effect {
        position: absolute;
        background-color: black;
        width: 100vw;
        height: 100vh;
        opacity: 100%;
    }

    h1 {
        font-family: "VT323";
        font-size: 4rem;
        color: white;
    }

    span {
        position: relative;    
    }

    #bottom {
        left: calc((1/3) * 100%);
        clip-path: inset(2.8rem 0 0 0);
        
    }

    #top {
        right: calc((1/3) * 100%);
        clip-path: inset(1.49rem 0 1.15rem 0);
        animation: top-glitch 1.5s ease infinite 1.5s alternate-reverse;
    }

    #middle {
        clip-path: inset(0 0 2.5rem 0);
        animation: middle-glitch 1.5s ease infinite 1.5s alternate-reverse;
    }

    @keyframes top-glitch {
        0% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.25rem blue;
            left: -22.6rem;

        }
        2% {
            text-shadow:0.25rem 0.25rem 0.15rem red, -0.25rem -0.25rem 0.15rem blue;
            left: -22.5rem;

        }
        4%{
            text-shadow: none;
            left: -22.4rem;
        }
        6% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.15rem blue;
            left: -22.2rem;
        } 
        7% {
            text-shadow: none;
            left: -22.2rem;
        }
        8% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.15rem blue;
            left: -22.2rem;
        }
        15% {
            text-shadow: none;
            left: -22.2rem;
        }
        15.5% {
            text-shadow:0.25rem 0.25rem 0.15rem red, -0.25rem -0.25rem 0.15rem blue;
            left: -22.5rem;
        }
        16% {
            text-shadow: none;
            left: -22.4rem;
        }
        100% {
            text-shadow: none;
            left: -22.4rem;  
        }
    }

    @keyframes middle-glitch {
        0% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.25rem blue;

        }
        2% {
            text-shadow:0.25rem 0.25rem 0.15rem red, -0.25rem -0.25rem 0.15rem blue;
            left: -.5rem;

        }
        4%{
            text-shadow: none;
            left: -.4rem;
        }
        6% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.15rem blue;
            left: .3rem;
        } 
        7% {
            text-shadow: none;
            left: 0rem;
        }
        8% {
            text-shadow:-0.25rem -0.25rem 0.15rem red, 0.25rem 0.25rem 0.15rem blue;
            left: 0rem;
        }
        15% {
            text-shadow: none;
            left: 0rem;
        }
        15.5% {
            text-shadow:0.25rem 0.25rem 0.15rem red, -0.25rem -0.25rem 0.15rem blue;
            left: 0rem;
        }
        16% {
            text-shadow: none;
        }
        100% {
            text-shadow: none; 
        }
    }
</style>