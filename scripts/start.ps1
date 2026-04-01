# 一键启动脚本（Windows）
# 创建数据目录
New-Item -ItemType Directory -Path .\data\mysql, .\data\redis, .\data\mongodb -Force

# 启动服务
docker-compose up -d

# 输出状态
Write-Host "✅ 本地开发环境启动完成！"
docker-compose ps