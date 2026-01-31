import { useParams } from 'react-router-dom'

export default function ProductDetailPage() {
  const { id } = useParams()

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="card">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">
          상품 상세 정보
        </h1>
        <p className="text-gray-600">
          상품 ID: {id}
        </p>
        <p className="text-gray-500 mt-4">
          상세 정보 기능은 준비 중입니다.
        </p>
      </div>
    </div>
  )
}
