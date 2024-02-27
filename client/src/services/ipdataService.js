import axios from "axios";

export default {
    getIp() {
        return axios.get(`https://api.ipdata.co?api-key=912d554c0f7ea148a03461305ca4eb64c4984c9d0ca77dc9d7518f5f`);
    }
}