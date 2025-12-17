import request from '@/utils/request'

/**
 * 生成预览
 * @param {Object} data - 预览生成参数
 * @param {number} data.workId - 作品ID
 * @param {number} data.productId - 商品ID
 * @param {string} data.color - 颜色
 * @param {string} data.size - 尺寸
 * @returns {Promise}
 */
export const generatePreview = (data) => {
  return request({
    url: '/preview/generate',
    method: 'POST',
    data
  })
}

/**
 * 查询任务状态
 * @param {number} taskId - 任务ID
 * @returns {Promise}
 */
export const getTaskStatus = (taskId) => {
  return request({
    url: `/preview/tasks/${taskId}`,
    method: 'GET'
  })
}

