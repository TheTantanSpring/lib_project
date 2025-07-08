# 1단계: 의존성 설치 (Base Stage)
FROM node:18-alpine AS base
WORKDIR /app

# pnpm 설치
RUN npm install -g pnpm

# 의존성 파일 복사 및 설치
COPY package.json pnpm-lock.yaml* ./
# lock 파일 불일치 문제를 해결하기 위해 lock 파일을 지우고 새로 설치합니다.
RUN rm -f pnpm-lock.yaml && pnpm install

# 2단계: 애플리케이션 빌드 (Build Stage)
FROM base AS build
WORKDIR /app
COPY . .

# 빌드 시 환경변수 전달
ARG NEXT_PUBLIC_API_BASE_URL
ARG NEXT_PUBLIC_KAKAO_MAP_API_KEY
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_KAKAO_MAP_API_KEY=$NEXT_PUBLIC_KAKAO_MAP_API_KEY

# 프로덕션용으로 애플리케이션을 빌드합니다.
# standalone 모드 설정에 따라 .next/standalone 폴더가 생성됩니다.
RUN pnpm build

# 3단계: 최종 이미지 실행 (Runner Stage)
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production

# 빌드 단계에서 생성된 standalone 결과물을 복사합니다.
COPY --from=build /app/.next/standalone ./
# public 폴더의 정적 에셋(이미지 등)을 복사합니다.
COPY --from=build /app/public ./public
# Next.js standalone 모드에서 필요한 정적 파일들을 복사합니다.
COPY --from=build /app/.next/static ./.next/static

EXPOSE 3000

# Next.js 서버를 실행합니다.
CMD ["node", "server.js"] 