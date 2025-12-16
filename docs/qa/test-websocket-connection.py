#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试 WebSocket 连接到小程序自动化服务
"""

import websocket
import time
import json

def test_websocket_connection(port=39411):
    """测试 WebSocket 连接"""
    url = f"ws://127.0.0.1:{port}"
    
    print(f"正在测试连接到: {url}")
    print("-" * 60)
    
    try:
        # 尝试连接
        print("1. 尝试建立 WebSocket 连接...")
        ws = websocket.create_connection(url, timeout=5)
        print("   ✓ WebSocket 连接成功！")
        
        # 尝试发送消息
        print("2. 尝试发送测试消息...")
        test_message = json.dumps({"method": "test", "params": {}})
        ws.send(test_message)
        print("   ✓ 消息发送成功")
        
        # 尝试接收消息
        print("3. 尝试接收响应...")
        try:
            result = ws.recv()
            print(f"   ✓ 收到响应: {result[:100]}...")
        except:
            print("   ⚠ 未收到响应（可能正常）")
        
        ws.close()
        print("-" * 60)
        print("✓ WebSocket 连接测试通过！")
        return True
        
    except websocket.WebSocketTimeoutException:
        print("   ✗ 连接超时")
        print("\n可能的原因：")
        print("   - 微信开发者工具中的'开启服务端口'未启用")
        print("   - 项目窗口未完全打开")
        return False
        
    except ConnectionRefusedError:
        print("   ✗ 连接被拒绝")
        print("\n可能的原因：")
        print("   - 端口未正确监听")
        print("   - 服务未启动")
        return False
        
    except Exception as e:
        print(f"   ✗ 连接失败: {e}")
        print("\n可能的原因：")
        print("   - 微信开发者工具中的'开启服务端口'未启用")
        print("   - 项目窗口未完全打开")
        print("   - WebSocket 服务未启动")
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("小程序自动化服务 WebSocket 连接测试")
    print("=" * 60)
    print()
    
    success = test_websocket_connection(39411)
    
    if not success:
        print()
        print("=" * 60)
        print("故障排查步骤：")
        print("=" * 60)
        print("1. 打开微信开发者工具")
        print("2. 确保项目窗口已打开（能看到小程序预览）")
        print("3. 点击：设置 -> 安全设置")
        print("4. 勾选：开启服务端口")
        print("5. 确认端口号为：39411")
        print("6. 等待 5-10 秒让服务完全启动")
        print("7. 重新运行此测试脚本")
        print()
        print("如果仍然失败，请检查：")
        print("- 防火墙是否阻止了连接")
        print("- 是否有其他程序占用了端口")
        print("- 微信开发者工具版本是否支持自动化")

