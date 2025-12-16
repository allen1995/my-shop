"use strict";
const common_vendor = require("../common/vendor.js");
async function generatePoster(options) {
  const {
    imageUrl,
    title = "我的AI作品",
    shareUrl = "",
    width = 750,
    height = 1334
  } = options;
  return new Promise((resolve, reject) => {
    const ctx = common_vendor.index.createCanvasContext("share-poster-canvas");
    const canvasWidth = width;
    const canvasHeight = height;
    ctx.setFillStyle("#ffffff");
    ctx.fillRect(0, 0, canvasWidth, canvasHeight);
    common_vendor.index.getImageInfo({
      src: imageUrl,
      success: async (imageRes) => {
        try {
          let drawQRPlaceholder = function(ctx2, canvasWidth2, qrY) {
            const qrSize = 200;
            const qrX = (canvasWidth2 - qrSize) / 2;
            ctx2.setFillStyle("#000000");
            ctx2.fillRect(qrX, qrY, qrSize, qrSize);
            ctx2.setFillStyle("#ffffff");
            ctx2.setFontSize(20);
            ctx2.setTextAlign("center");
            ctx2.fillText("二维码", canvasWidth2 / 2, qrY + qrSize / 2);
          }, drawPoster = function(ctx2, canvasWidth2, canvasHeight2) {
            ctx2.setFillStyle("#999999");
            ctx2.setFontSize(20);
            const bottomY = canvasHeight2 - 40;
            ctx2.fillText("AI印花电商小程序", canvasWidth2 / 2, bottomY);
            ctx2.draw(false, () => {
              setTimeout(() => {
                common_vendor.index.canvasToTempFilePath({
                  canvasId: "share-poster-canvas",
                  width: canvasWidth2,
                  height: canvasHeight2,
                  destWidth: canvasWidth2,
                  destHeight: canvasHeight2,
                  success: (res) => {
                    resolve(res.tempFilePath);
                  },
                  fail: (err) => {
                    reject(err);
                  }
                });
              }, 500);
            });
          };
          const imageMaxWidth = canvasWidth - 80;
          const imageMaxHeight = canvasHeight - 400;
          const imageAspectRatio = imageRes.width / imageRes.height;
          let imageWidth = imageMaxWidth;
          let imageHeight = imageMaxWidth / imageAspectRatio;
          if (imageHeight > imageMaxHeight) {
            imageHeight = imageMaxHeight;
            imageWidth = imageMaxHeight * imageAspectRatio;
          }
          const imageX = (canvasWidth - imageWidth) / 2;
          const imageY = 60;
          ctx.drawImage(imageUrl, imageX, imageY, imageWidth, imageHeight);
          ctx.setFillStyle("#333333");
          ctx.setFontSize(36);
          ctx.setTextAlign("center");
          const titleY = imageY + imageHeight + 60;
          const maxTitleWidth = canvasWidth - 80;
          const titleLines = wrapText(ctx, title, maxTitleWidth, 36);
          titleLines.forEach((line, index) => {
            ctx.fillText(line, canvasWidth / 2, titleY + index * 50);
          });
          ctx.setFillStyle("#666666");
          ctx.setFontSize(24);
          const hintY = titleY + titleLines.length * 50 + 30;
          ctx.fillText("长按识别二维码查看", canvasWidth / 2, hintY);
          if (shareUrl) {
            try {
              const qrCodeUrl = await generateQRCode(shareUrl, 200);
              if (qrCodeUrl) {
                common_vendor.index.getImageInfo({
                  src: qrCodeUrl,
                  success: (qrRes) => {
                    const qrSize = 200;
                    const qrX = (canvasWidth - qrSize) / 2;
                    const qrY = hintY + 50;
                    ctx.drawImage(qrCodeUrl, qrX, qrY, qrSize, qrSize);
                    drawPoster(ctx, canvasWidth, canvasHeight);
                  },
                  fail: () => {
                    drawQRPlaceholder(ctx, canvasWidth, hintY + 50);
                    drawPoster(ctx, canvasWidth, canvasHeight);
                  }
                });
              } else {
                drawQRPlaceholder(ctx, canvasWidth, hintY + 50);
                drawPoster(ctx, canvasWidth, canvasHeight);
              }
            } catch (error) {
              drawQRPlaceholder(ctx, canvasWidth, hintY + 50);
              drawPoster(ctx, canvasWidth, canvasHeight);
            }
          } else {
            drawPoster(ctx, canvasWidth, canvasHeight);
          }
        } catch (error) {
          reject(error);
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}
function wrapText(ctx, text, maxWidth, fontSize) {
  const charsPerLine = Math.floor(maxWidth / (fontSize * 0.6));
  const lines = [];
  if (text.length <= charsPerLine) {
    return [text];
  }
  for (let i = 0; i < text.length; i += charsPerLine) {
    lines.push(text.substring(i, i + charsPerLine));
  }
  return lines.length > 0 ? lines : [text];
}
async function savePosterToAlbum(imagePath) {
  return new Promise((resolve, reject) => {
    common_vendor.index.saveImageToPhotosAlbum({
      filePath: imagePath,
      success: () => {
        common_vendor.index.showToast({
          title: "保存成功",
          icon: "success"
        });
        resolve();
      },
      fail: (err) => {
        if (err.errMsg.includes("auth deny")) {
          common_vendor.index.showModal({
            title: "提示",
            content: "需要授权访问相册",
            success: (modalRes) => {
              if (modalRes.confirm) {
                common_vendor.index.openSetting();
              }
            }
          });
        } else {
          common_vendor.index.showToast({
            title: "保存失败",
            icon: "none"
          });
        }
        reject(err);
      }
    });
  });
}
async function generateQRCode(text, size = 200) {
  return new Promise((resolve) => {
    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(text)}`;
    common_vendor.index.getImageInfo({
      src: qrUrl,
      success: () => {
        resolve(qrUrl);
      },
      fail: () => {
        resolve("");
      }
    });
  });
}
exports.generatePoster = generatePoster;
exports.savePosterToAlbum = savePosterToAlbum;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/poster.js.map
