'use client';

const scanner = require('node-wifi-scanner');


export default function GetWifiBtn(){

    function print(){
        scanner.scan((err, networks) => {
        if (err) {
            console.error(err);
            return;
        }
        console.log(networks);
        });
    }

    return(
            <button type="button" onClick={print} >Effect recognition</button>
    )
}