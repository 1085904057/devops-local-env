#!/bin/bash
# 一键启动所有本地开发中间件服务
set -e  # 出错时退出

# 创建数据目录（避免挂载时目录不存在）
mkdir -p ./data/mysql ./data/redis ./data/mongodb

# 启动所有服务（-d：后台运行）
docker-compose up -d

# 输出启动成功提示
echo "✅ 本地开发环境启动完成！"
echo "🔍 已启动服务："
docker-compose ps