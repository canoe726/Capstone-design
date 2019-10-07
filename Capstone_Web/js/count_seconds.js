var rand;
self.onmessage = function (e) {
    rand =  10 * 3000 ; // 0~9��
    postLoop();
};

function postLoop() {

    setTimeout(function () {
        postMessage('check');
    }, 20000 + rand);  //10��
}
