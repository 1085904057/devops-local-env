## 1. Windows系统安装WSL 2
这是Docker Desktop推荐的安装方式
https://learn.microsoft.com/en-us/windows/wsl/install
```shell
## 安装wsl
wsl --install
## 查看可安装的Linux发行版
wsl --list --online
## 安装Ubuntu
wsl --install Ubuntu
## 验证运行的发布版
wsl -l -v
```
![管理员身份安装](./images/2042282543710800.png)
![发行版设置账号密码](./images/2042249580061200.png)
administrator/root

## 2. Windows系统安装Docker Desktop
https://docs.docker.com/desktop/features/wsl/

### 2.1 安装Docker Desktop
![下载安装包并安装](./images/2042843426145000.png)
![使用默认的安装配置](./images/2042745682725300.png)

### 2.2 注册Docker Desktop账号
![注册Docker Desktop账号](./images/1504851660200.png)
![用Google邮箱注册](./images/578935677900.png)

### 2.3 设置Docker Desktop
![使用WSL2 Engine](./images/1675587808800.png)
![使用Ubuntu发布版](./images/2137522447299.png)
![目前有两个发布版](./images/1900113814600.png)
![修改文件存储目录](./images/2365526714100.png)

## 3. 拷贝开发环境Devops项目
### 3.1. 克隆仓库
```shell
git clone https://github.com/1085904057/devops-local-env.git
cd devops-local-env
```
### 3.2. 启动容器
```shell
# 给脚本添加执行权限（Linux/Mac）
chmod +x ./scripts/start.sh ./scripts/stop.sh

# Linux用户
./scripts/start.sh

# Windows用户
.\scripts\start.ps1
```
![start执行结果](./images/3924583410700.png)

## 4. 运行服务并验证
```shell
# 查看所有服务状态
docker-compose ps

# 查看MySQL日志
docker logs local-mysql

# 连接测试（以MySQL为例）
mysql -h 127.0.0.1 -P 3306 -u root -p123456
```