"use client"

import { useLayoutEffect, useRef, useState } from "react"
import type { Library } from "@/types/library"

interface KakaoMapProps {
  libraries: Library[]
  selectedLibrary?: Library
  width?: string
  height?: string
  className?: string
}

declare global {
  interface Window {
    kakao: any
  }
}

export default function KakaoMap({
  libraries,
  selectedLibrary,
  width = "100%",
  height = "400px",
  className = "",
}: KakaoMapProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  const mapRef = useRef<any>(null)
  const markersRef = useRef<any[]>([])

  const [sdkReady, setSdkReady] = useState(false)
  const [isMapInitialized, setIsMapInitialized] = useState(false)
  const [loadError, setLoadError] = useState<string | null>(null)

  // 1. 카카오 SDK 로드
  useLayoutEffect(() => {
    const appKey = process.env.NEXT_PUBLIC_KAKAO_MAP_API_KEY
    console.log("🔑 카카오 API 키:", appKey ? "설정됨" : "없음")
    console.log("🔑 실제 API 키:", appKey)
    
    if (!appKey) {
      setLoadError("카카오 API 키가 설정되지 않았습니다.")
      return
    }

    if (window.kakao && window.kakao.maps) {
      setSdkReady(true)
      return
    }

    const script = document.createElement("script")
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${appKey}&autoload=false`
    script.async = true
    
    script.onload = () => {
      window.kakao.maps.load(() => {
        console.log("✅ 카카오 지도 SDK 로드 완료")
        setSdkReady(true)
      })
    }
    
    script.onerror = () => {
      console.error("❌ 카카오 지도 API 로드 실패")
      setLoadError("카카오 지도 API 로드에 실패했습니다.")
    }

    document.head.appendChild(script)

    return () => {
      if (document.head.contains(script)) {
        document.head.removeChild(script)
      }
    }
  }, [])

  // 2. SDK 준비 및 라이브러리 데이터 변경 시 지도 초기화/업데이트
  useLayoutEffect(() => {
    if (!sdkReady || !containerRef.current) {
      return
    }

    const container = containerRef.current
    let map = mapRef.current

    // 지도가 아직 초기화되지 않았다면 새로 생성
    if (!map) {
      console.log("🗺️ 지도 최초 생성")
      const centerLat = selectedLibrary?.latitude ?? 37.5665
      const centerLng = selectedLibrary?.longitude ?? 126.978
      
      const options = {
        center: new window.kakao.maps.LatLng(centerLat, centerLng),
        level: selectedLibrary ? 3 : 8,
      }
      map = new window.kakao.maps.Map(container, options)
      mapRef.current = map
      setIsMapInitialized(true)
    }

    // 기존 마커 모두 제거
    markersRef.current.forEach(marker => marker.setMap(null))
    markersRef.current = []

    // 새로운 마커 추가
    console.log(`📍 마커 ${libraries.length}개 추가`)
    libraries.forEach(lib => {
      const markerPosition = new window.kakao.maps.LatLng(lib.latitude, lib.longitude)
      const marker = new window.kakao.maps.Marker({ position: markerPosition })
      
      const infowindowContent = `
        <div style="padding: 10px; font-size: 12px; min-width: 200px;">
          <strong>${lib.name}</strong>
        </div>
      `
      const infowindow = new window.kakao.maps.InfoWindow({ content: infowindowContent })

      window.kakao.maps.event.addListener(marker, 'click', () => {
        infowindow.open(map, marker)
      })

      marker.setMap(map)
      markersRef.current.push(marker)
    })

    // 선택된 도서관이 있다면 지도를 중심으로 이동하고 확대
    if (selectedLibrary) {
      console.log(`🎯 선택된 도서관으로 이동: ${selectedLibrary.name}`)
      const moveLatLng = new window.kakao.maps.LatLng(selectedLibrary.latitude, selectedLibrary.longitude)
      map.setCenter(moveLatLng)
      map.setLevel(3, { animate: true })
      
      // 선택된 도서관의 마커를 찾아 인포윈도우 열기
      const selectedMarker = markersRef.current.find(
        (marker) => marker.getPosition().getLat() === selectedLibrary.latitude && marker.getPosition().getLng() === selectedLibrary.longitude
      )
      if(selectedMarker) {
        // 인포윈도우는 클릭 시에만 열리도록 유지 (자동 오픈 제거)
      }
    } else if (libraries.length > 0) {
        // 목록이 있지만 선택된 것이 없으면 전체 뷰
        map.setLevel(8, { animate: true })
    }

  }, [sdkReady, libraries, selectedLibrary])

  return (
    <div style={{ position: 'relative', width, height }} className={className}>
      <div 
        ref={containerRef} 
        style={{ width: '100%', height: '100%' }} 
        className="rounded-lg bg-muted"
      />
      
      {loadError && (
        <div className="absolute inset-0 flex flex-col items-center justify-center bg-red-500/20 rounded-lg">
          <p className="text-sm font-semibold text-red-700">지도 로딩 실패</p>
          <p className="text-xs text-red-600 mt-1">{loadError}</p>
        </div>
      )}
      
      {!isMapInitialized && !loadError && (
        <div className="absolute inset-0 flex items-center justify-center bg-muted/80 rounded-lg backdrop-blur-sm">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-2"></div>
            <p className="text-sm text-muted-foreground">
              {sdkReady ? '지도 초기화 중...' : '지도 SDK 로딩 중...'}
            </p>
          </div>
        </div>
      )}
    </div>
  )
}
