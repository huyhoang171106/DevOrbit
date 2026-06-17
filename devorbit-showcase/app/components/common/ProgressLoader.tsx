import dynamic from 'next/dynamic';
import { useEffect, useState } from 'react';
import { isMobile } from 'react-device-detect';

const ProgressLoader = ({ progress }: { progress: number }) => {
  const strokeWidth = 3;
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
  });

  useEffect(() => {
    function handleResize() {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    }

    window.addEventListener('resize', handleResize);
    handleResize();

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const clampedProgress = Math.max(0, Math.min(100, progress));

  const svgWidth = Math.max(0, windowSize.width - 16);
  const svgHeight = Math.max(0, windowSize.height - 16);

  const halfStroke = 1;
  const rectWidth = Math.max(0, svgWidth - strokeWidth);
  const rectHeight = Math.max(0, svgHeight - strokeWidth);

  const perimeter = rectWidth > 0 && rectHeight > 0 ? (rectWidth * 2) + (rectHeight * 2) : 0;
  const strokeDashoffset = perimeter - (perimeter * clampedProgress) / 100;

  if (svgWidth <= strokeWidth || svgHeight <= strokeWidth) {
      return null;
  }

  if (isMobile) {
    return (
      <div className="fixed top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none">
        <div className="relative w-[100px] transition-all duration-500 font-sans font-bold"
          style={{ opacity: progress === 100 ? 0 : 0.7, fontSize: '0.6rem' }}>
          <div className='absolute w-[100px] bg-black opacity-30 h-[2px]'/>
          <div
            className="absolute transition-all duration-500 ease-in-out "
            style={{
              height: '2px',
              width: `${progress}%`,
              backgroundColor: 'white',
            }}
          />
          <div className='mt-2 text-white'>
            {`${progress.toFixed(2)}%`}
          </div>
        </div>
      </div>
    );
  }
  return (
    <div
      className="fixed top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none"
      style={{ padding: '1rem' }}
    >
      <svg
        width={svgWidth}
        height={svgHeight}
        viewBox={`0 0 ${svgWidth} ${svgHeight}`}
        style={{ display: svgWidth > 0 && svgHeight > 0 ? 'block' : 'none' }}
      >
        <rect
          x={halfStroke}
          y={halfStroke}
          width={rectWidth}
          height={rectHeight}
          fill="none"
          strokeWidth={strokeWidth}
          stroke="rgba(0, 0, 0, 0.2)"
        />
        <rect
          x={halfStroke}
          y={halfStroke}
          width={rectWidth}
          height={rectHeight}
          fill="none"
          strokeWidth={strokeWidth}
          stroke="rgba(255, 255, 255, 0.7)"
          style={{
            strokeDasharray: perimeter,
            strokeDashoffset: strokeDashoffset,
            transition: 'stroke-dashoffset 1s ease-in-out',
          }}
        />
      </svg>
    </div>
  );
};

export default dynamic(() => Promise.resolve(ProgressLoader), { ssr: false });
