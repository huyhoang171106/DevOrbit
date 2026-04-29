import { Link } from 'react-router-dom'

export function HomePage() {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center text-center">
      <h1 className="text-4xl font-bold text-gray-900">Welcome to DevOrbit</h1>
      <p className="mt-3 max-w-md text-gray-600">
        Discover course-related GitHub repositories, explore tech stacks, and find the
        perfect resources for your learning journey.
      </p>
      <Link
        to="/courses"
        className="mt-6 rounded-lg bg-blue-600 px-6 py-3 text-sm font-semibold text-white shadow hover:bg-blue-700"
      >
        Browse Courses
      </Link>
    </div>
  )
}
