// window.requestAnimationFrame = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame;

var image = document.querySelector('.image');
var pixelation = new Pixelate(image);

var slider = document.querySelector('.slider');
var output = document.getElementById('output');

slider.addEventListener('input', function(event) {
  var amount = event.currentTarget.value;
  update(amount);
});

function update(amount) {
  output.textContent = Math.round(amount) + '%';
  pixelation.setAmount(amount / 100).render();
}

// window.onresize = function() {
//   pixelation.setWidth(image.parentNode.clientWidth).render();
// };

update(slider.value)

    // Method 2: Manual pixelation

    // let c = document.createElement("canvas");
    // ctx = c.getContext('2d');
    // let img1 = new Image();

    // img1.onload = function () {
    //   document.getElementById("image1").remove();

    //   w = img1.width;
    //   h = img1.height;

    //   c.width = w;
    //   c.height = h;
    //   ctx.drawImage(img1, 0, 0);

    //   var pixelArr = ctx.getImageData(0, 0, w, h).data;
    //   sample_size = 128;

    //   for (let y = 0; y < h; y += sample_size) {
    //     for (let x = 0; x < w; x += sample_size) {
    //       let p = (x + (y*w)) * 4;
    //       ctx.fillStyle = "rgba(" + pixelArr[p] + "," + pixelArr[p + 1] + "," + pixelArr[p + 2] + "," + pixelArr[p + 3] + ")";
    //       ctx.fillRect(x, y, sample_size, sample_size);
    //     }
    //   }

    //   let img2 = new Image();
    //   img2.src = c.toDataURL("image/jpeg");
    //   img2.width = 600;
    //   document.body.appendChild(img2);
    // };

    // img1.src = document.getElementById("image1").src;
