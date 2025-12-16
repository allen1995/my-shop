/**
 * 海报生成工具
 */

/**
 * 生成分享海报
 * @param {Object} options 配置选项
 * @param {String} options.imageUrl 作品图片URL
 * @param {String} options.title 作品标题
 * @param {String} options.shareUrl 分享链接
 * @param {Number} options.width 画布宽度，默认750
 * @param {Number} options.height 画布高度，默认1334
 * @returns {Promise<String>} 返回生成的图片临时路径
 */
export async function generatePoster(options) {
  const {
    imageUrl,
    title = '我的AI作品',
    shareUrl = '',
    width = 750,
    height = 1334
  } = options

  return new Promise((resolve, reject) => {
    // 创建canvas上下文
    const ctx = uni.createCanvasContext('share-poster-canvas')
    
    // 设置画布尺寸
    const canvasWidth = width
    const canvasHeight = height
    
    // 背景色
    ctx.setFillStyle('#ffffff')
    ctx.fillRect(0, 0, canvasWidth, canvasHeight)
    
    // 加载作品图片
    uni.getImageInfo({
      src: imageUrl,
      success: async (imageRes) => {
        try {
          // 计算图片显示尺寸（保持比例）
          const imageMaxWidth = canvasWidth - 80 // 左右各40rpx边距
          const imageMaxHeight = canvasHeight - 400 // 预留底部空间
          const imageAspectRatio = imageRes.width / imageRes.height
          let imageWidth = imageMaxWidth
          let imageHeight = imageMaxWidth / imageAspectRatio
          
          if (imageHeight > imageMaxHeight) {
            imageHeight = imageMaxHeight
            imageWidth = imageMaxHeight * imageAspectRatio
          }
          
          const imageX = (canvasWidth - imageWidth) / 2
          const imageY = 60 // 顶部边距
          
          // 绘制作品图片
          ctx.drawImage(imageUrl, imageX, imageY, imageWidth, imageHeight)
          
          // 绘制标题
          ctx.setFillStyle('#333333')
          ctx.setFontSize(36)
          ctx.setTextAlign('center')
          const titleY = imageY + imageHeight + 60
          // 处理标题过长的情况
          const maxTitleWidth = canvasWidth - 80
          const titleLines = wrapText(ctx, title, maxTitleWidth, 36)
          titleLines.forEach((line, index) => {
            ctx.fillText(line, canvasWidth / 2, titleY + index * 50)
          })
          
          // 绘制提示文字
          ctx.setFillStyle('#666666')
          ctx.setFontSize(24)
          const hintY = titleY + titleLines.length * 50 + 30
          ctx.fillText('长按识别二维码查看', canvasWidth / 2, hintY)
          
          // 生成二维码（如果提供了分享链接）
          if (shareUrl) {
            try {
              // 尝试生成二维码
              const qrCodeUrl = await generateQRCode(shareUrl, 200)
              if (qrCodeUrl) {
                // 等待二维码图片加载
                uni.getImageInfo({
                  src: qrCodeUrl,
                  success: (qrRes) => {
                    const qrSize = 200
                    const qrX = (canvasWidth - qrSize) / 2
                    const qrY = hintY + 50
                    ctx.drawImage(qrCodeUrl, qrX, qrY, qrSize, qrSize)
                    drawPoster(ctx, canvasWidth, canvasHeight)
                  },
                  fail: () => {
                    // 二维码加载失败，绘制占位符
                    drawQRPlaceholder(ctx, canvasWidth, hintY + 50)
                    drawPoster(ctx, canvasWidth, canvasHeight)
                  }
                })
              } else {
                // 二维码生成失败，绘制占位符
                drawQRPlaceholder(ctx, canvasWidth, hintY + 50)
                drawPoster(ctx, canvasWidth, canvasHeight)
              }
            } catch (error) {
              // 二维码生成失败，绘制占位符
              drawQRPlaceholder(ctx, canvasWidth, hintY + 50)
              drawPoster(ctx, canvasWidth, canvasHeight)
            }
          } else {
            drawPoster(ctx, canvasWidth, canvasHeight)
          }
          
          function drawQRPlaceholder(ctx, canvasWidth, qrY) {
            const qrSize = 200
            const qrX = (canvasWidth - qrSize) / 2
            // 绘制二维码占位框
            ctx.setFillStyle('#000000')
            ctx.fillRect(qrX, qrY, qrSize, qrSize)
            // 绘制占位文字
            ctx.setFillStyle('#ffffff')
            ctx.setFontSize(20)
            ctx.setTextAlign('center')
            ctx.fillText('二维码', canvasWidth / 2, qrY + qrSize / 2)
          }
          
          function drawPoster(ctx, canvasWidth, canvasHeight) {
            // 绘制底部信息
            ctx.setFillStyle('#999999')
            ctx.setFontSize(20)
            const bottomY = canvasHeight - 40
            ctx.fillText('AI印花电商小程序', canvasWidth / 2, bottomY)
            
            // 执行绘制
            ctx.draw(false, () => {
              // 延迟一下确保绘制完成
              setTimeout(() => {
                // 导出图片
                uni.canvasToTempFilePath({
                  canvasId: 'share-poster-canvas',
                  width: canvasWidth,
                  height: canvasHeight,
                  destWidth: canvasWidth,
                  destHeight: canvasHeight,
                  success: (res) => {
                    resolve(res.tempFilePath)
                  },
                  fail: (err) => {
                    reject(err)
                  }
                })
              }, 500)
            })
          }
        } catch (error) {
          reject(error)
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 文本换行处理（简化版，按字符数换行）
 */
function wrapText(ctx, text, maxWidth, fontSize) {
  // uni-app的Canvas API可能不支持measureText，使用简化方案
  // 根据字体大小估算每行可容纳的字符数
  const charsPerLine = Math.floor(maxWidth / (fontSize * 0.6)) // 估算：字符宽度约为字体大小的0.6倍
  const lines = []
  
  // 如果文本长度小于等于每行字符数，直接返回
  if (text.length <= charsPerLine) {
    return [text]
  }
  
  // 按字符数分割
  for (let i = 0; i < text.length; i += charsPerLine) {
    lines.push(text.substring(i, i + charsPerLine))
  }
  
  return lines.length > 0 ? lines : [text]
}

/**
 * 保存海报到相册
 * @param {String} imagePath 图片路径
 * @returns {Promise}
 */
export async function savePosterToAlbum(imagePath) {
  return new Promise((resolve, reject) => {
    uni.saveImageToPhotosAlbum({
      filePath: imagePath,
      success: () => {
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        })
        resolve()
      },
      fail: (err) => {
        if (err.errMsg.includes('auth deny')) {
          uni.showModal({
            title: '提示',
            content: '需要授权访问相册',
            success: (modalRes) => {
              if (modalRes.confirm) {
                uni.openSetting()
              }
            }
          })
        } else {
          uni.showToast({
            title: '保存失败',
            icon: 'none'
          })
        }
        reject(err)
      }
    })
  })
}

/**
 * 生成二维码（使用在线API，实际项目中应使用后端API或插件）
 * @param {String} text 二维码内容
 * @param {Number} size 二维码尺寸
 * @returns {Promise<String>} 返回二维码图片URL
 */
export async function generateQRCode(text, size = 200) {
  // 实际项目中应该：
  // 1. 使用后端API生成二维码（推荐）
  // 2. 或使用uni-app插件市场的二维码生成插件
  // 3. 或使用canvas绘制二维码（需要二维码算法库）
  
  // 这里使用在线API作为临时方案
  return new Promise((resolve) => {
    // 使用在线二维码生成服务（临时方案）
    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(text)}`
    
    // 验证URL是否可访问
    uni.getImageInfo({
      src: qrUrl,
      success: () => {
        resolve(qrUrl)
      },
      fail: () => {
        // 如果在线API失败，返回空字符串，使用占位符
        resolve('')
      }
    })
  })
}

