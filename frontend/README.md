# 도서관 시스템 (Library Management System)

도서관 이용자들이 온라인으로 도서관을 검색하고 위치를 확인할 수 있는 웹 애플리케이션입니다.

## 🚀 기술 스택

### 프론트엔드
- **Next.js 15** - React 기반 풀스택 프레임워크
- **React 18** - 사용자 인터페이스 라이브러리
- **TypeScript** - 타입 안전성을 위한 JavaScript 확장
- **Tailwind CSS** - 유틸리티 우선 CSS 프레임워크
- **shadcn/ui** - 재사용 가능한 UI 컴포넌트 라이브러리

### 외부 API
- **카카오 지도 API** - 지도 서비스 및 위치 정보
- **Spring Boot REST API** - 백엔드 데이터 서비스

## 📁 프로젝트 구조

\`\`\`
library-system/
├── app/                    # Next.js App Router
│   ├── api/               # API Routes (백엔드 프록시)
│   │   └── libraries/     # 도서관 API 프록시
│   ├── libraries/         # 도서관 관련 페이지
│   │   ├── [id]/         # 도서관 상세 페이지
│   │   └── page.tsx      # 도서관 목록 페이지
│   ├── layout.tsx         # 루트 레이아웃
│   ├── page.tsx          # 홈페이지
│   └── globals.css       # 전역 스타일
├── components/            # React 컴포넌트
│   ├── ui/               # shadcn/ui 기본 컴포넌트
│   └── kakao-map.tsx     # 카카오 지도 컴포넌트
├── services/             # API 서비스 레이어
│   └── library-api.ts    # 도서관 API 호출 함수
├── types/                # TypeScript 타입 정의
│   └── library.ts        # 도서관 관련 타입
├── package.json          # 의존성 관리
├── tsconfig.json         # TypeScript 설정
├── tailwind.config.ts    # Tailwind CSS 설정
└── next.config.mjs       # Next.js 설정
\`\`\`

## 🛠️ 로컬 개발 환경 설정

### 1. 프로젝트 클론 및 의존성 설치

\`\`\`bash
# 의존성 설치
npm install
# 또는
yarn install
# 또는
pnpm install
\`\`\`

### 2. 환경변수 설정

프로젝트 루트에 `.env.local` 파일을 생성하고 다음 내용을 추가하세요:

\`\`\`env
# 백엔드 API URL
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

# 카카오 지도 API 키
NEXT_PUBLIC_KAKAO_MAP_API_KEY=your_kakao_map_api_key_here
\`\`\`

### 3. 카카오 지도 API 키 발급

1. [카카오 개발자 센터](https://developers.kakao.com/) 접속
2. **내 애플리케이션** → **애플리케이션 추가하기**
3. 앱 이름 입력 후 생성
4. **플랫폼 설정** → **Web 플랫폼 등록**
   - 사이트 도메인: `http://localhost:3000` (개발용)
5. **JavaScript 키** 복사하여 환경변수에 설정

### 4. 백엔드 서버 실행

Spring Boot 백엔드 서버가 `http://localhost:8080`에서 실행되고 있어야 합니다.

### 5. 개발 서버 실행

\`\`\`bash
npm run dev
# 또는
yarn dev
# 또는
pnpm dev
\`\`\`

브라우저에서 [http://localhost:3000](http://localhost:3000)을 열어 확인하세요.

## 🏗️ Next.js란 무엇인가요?

### Next.js 개요
**Next.js**는 React 기반의 **풀스택 웹 프레임워크**입니다. React만으로는 클라이언트 사이드 렌더링(CSR)만 가능하지만, Next.js는 다음과 같은 추가 기능을 제공합니다:

### 주요 특징
- **서버 사이드 렌더링(SSR)** - 서버에서 HTML을 미리 생성
- **정적 사이트 생성(SSG)** - 빌드 시점에 HTML 생성
- **API Routes** - 백엔드 API 엔드포인트 생성 가능
- **파일 기반 라우팅** - 폴더 구조로 자동 라우팅
- **이미지 최적화** - 자동 이미지 압축 및 최적화
- **자동 코드 분할** - 페이지별 번들 최적화

### 이 프로젝트에서의 Next.js 역할

\`\`\`
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   사용자 브라우저   │    │   Next.js 앱     │    │  Spring Boot    │
│                │    │  (프론트엔드)      │    │   (백엔드)       │
│                │◄──►│                │◄──►│                │
│  - UI 렌더링     │    │  - React 컴포넌트  │    │  - 비즈니스 로직   │
│  - 사용자 상호작용 │    │  - API 프록시     │    │  - 데이터베이스    │
│                │    │  - 라우팅        │    │  - REST API     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
\`\`\`

### Next.js vs Spring Boot

| 구분 | Next.js (이 프로젝트) | Spring Boot (백엔드) |
|------|---------------------|---------------------|
| **주요 역할** | 프론트엔드 + API 프록시 | 백엔드 비즈니스 로직 |
| **담당 업무** | UI 렌더링, 라우팅, 사용자 경험 | 데이터 처리, 비즈니스 규칙, DB 연동 |
| **API Routes** | 프록시 역할 (CORS 해결) | 실제 데이터 처리 |
| **데이터베이스** | 직접 연결 안함 | 직접 연결 및 관리 |

### 왜 Next.js API Routes를 사용했나요?

1. **CORS 문제 해결** - 브라우저에서 직접 `localhost:8080` 호출 시 CORS 에러 방지
2. **보안** - API 키나 민감한 정보를 서버에서 처리
3. **에러 처리** - 백엔드 연결 실패 시 graceful fallback 제공
4. **개발 편의성** - 프론트엔드와 백엔드를 하나의 도메인에서 통합 관리

## 🌐 배포

### Vercel 배포 (권장)
1. GitHub에 코드 푸시
2. [Vercel](https://vercel.com)에서 프로젝트 import
3. 환경변수 설정
4. 자동 배포 완료

### 환경변수 (배포용)
\`\`\`env
NEXT_PUBLIC_API_BASE_URL=https://your-backend-domain.com
NEXT_PUBLIC_KAKAO_MAP_API_KEY=your_production_api_key
\`\`\`

## 📋 주요 기능

- ✅ 도서관 목록 조회 및 검색
- ✅ 도서관 상세 정보 확인
- ✅ 카카오 지도를 통한 위치 확인
- ✅ 길찾기 기능
- ✅ 반응형 디자인 (모바일 지원)
- ✅ 페이지네이션
- ✅ 에러 처리 및 로딩 상태

## 🔧 개발 스크립트

\`\`\`bash
# 개발 서버 실행
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버 실행
npm run start

# 린팅
npm run lint

# 타입 체크
npm run type-check
\`\`\`

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
