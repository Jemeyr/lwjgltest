#version 150

out vec4 outColor;

in vec3 colorPS;
in vec2 texCoordPS;

uniform sampler2D tex;

void main() 
{
	if(texture(tex, texCoordPS) != 0.0)
	{
		outColor = texture(tex, texCoordPS);// * vec4(colorPS, 1.0);
	}
	else
	{
		outColor = vec4(0.0, 0.0, 0.0, 1.0);
	}
	//outColor = vec4(texCoordPS.x, texCoordPS.y, 1.0, 1.0);

}

