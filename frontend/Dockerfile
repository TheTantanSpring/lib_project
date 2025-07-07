# 1단계: 의존성 설치 및 기본 설정 (Base Stage)
# node 버전을 프로젝트의 package.json과 맞추는 것이 좋습니다.
FROM node:18-alpine AS base
WORKDIR /app

# pnpm 설치 (만약 npm이나 yarn을 사용한다면 해당 명령어로 변경)
RUN npm install -g pnpm

# 의존성 파일만 먼저 복사하여 Docker의 레이어 캐시를 활용합니다.
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile


# 2단계: 애플리케이션 빌드 (Build Stage)
FROM base AS build
WORKDIR /app

# 환경변수를 빌드 시점에 전달받습니다.
ARG NEXT_PUBLIC_API_BASE_URL
ARG NEXT_PUBLIC_KAKAO_MAP_API_KEY

# 환경변수 설정
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_KAKAO_MAP_API_KEY=$NEXT_PUBLIC_KAKAO_MAP_API_KEY

COPY . .
# 프로덕션용으로 애플리케이션을 빌드합니다.
# standalone 모드 설정에 따라 .next/standalone 폴더가 생성됩니다.
RUN pnpm build


# 3단계: 최종 이미지 실행 (Runner Stage)
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
# 백엔드 API 서버의 주소를 환경변수로 받습니다.
# 이 값은 docker-compose.yml에서 설정할 예정입니다.
ENV NEXT_PUBLIC_API_BASE_URL="http://localhost:8080"

# 빌드 단계에서 생성된 standalone 결과물을 복사합니다.
COPY --from=build /app/.next/standalone ./
# public 폴더의 정적 에셋(이미지 등)을 복사합니다.
COPY --from=build /app/public ./public
# Next.js standalone 모드에서 필요한 정적 파일들을 복사합니다.
COPY --from=build /app/.next/static ./.next/static

EXPOSE 3000

# Next.js 서버를 실행합니다.
CMD ["node", "server.js"] 