import { Logger, ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { json, urlencoded } from 'express';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.use(json({ limit: '500mb' }));
  app.use(urlencoded({ extended: true, limit: '500mb' }));
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      transformOptions: {
        enableImplicitConversion: true,
      },
    }),
  );
  const port = process.env.PORT ?? 3000;
  await app.listen(port);
  Logger.log(`Server running at ${await app.getUrl()}`, 'Bootstrap');
}
bootstrap();
