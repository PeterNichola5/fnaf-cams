<template>
    <div></div>
    <div id="backdrop">
        <h1>
            <span id="bottom" style="--index: 0;">PETER NICHOLAS</span>
            <span id="middle" style="--index: 1;">PETER NICHOLAS</span>
            <span id="top" style="--index: 2;">PETER NICHOLAS</span>
        </h1>
    </div>
</template>

<script>
</script>

<style scoped>
    #backdrop {
        background-color: black;
        width: 100vw;
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
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
        clip-path: inset(1.5rem 0 1.2rem 0);
        animation: top-glitch 1.5s ease infinite 1.5s alternate-reverse;
    }

    #middle {
        clip-path: inset(0 0 2.5rem 0);
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
</style>