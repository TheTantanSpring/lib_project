export interface Library {
  id: number
  name: string
  address: string
  phone: string
  latitude: number
  longitude: number
  openingHours: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: number
}

export interface LibraryListResponse {
  libraries: Library[]
  totalCount: number
  page: number
  size: number
}
